{
  description = "Redblack tree visualization";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};

        packageJSON = pkgs.lib.importJSON ./package.json;

        # Generate node modules derivation with buildNpmPackage
        npmDeps = pkgs.buildNpmPackage {
          name = "rbtv-deps";
          version = packageJSON.version;
          src = ./.;
          # Make sure this hash is correct for your package-lock.json
          npmDepsHash = "sha256-zjpHXt0tL+Fxyawrx/j//dV7iHsV1GPVqlOUMy2ox48=";

          # Skip the build step - we only want the dependencies installed
          dontNpmBuild = true;

          # Install node_modules into a lib subdirectory
          installPhase = ''
            mkdir -p $out/lib/node_modules
            cp -r node_modules/. $out/lib/node_modules/
          '';
        };

      in {
        # Build using stdenv.mkDerivation for full control
        packages.default = pkgs.stdenv.mkDerivation {
          pname = "rbtv";
          version = "0.1.0";
          src = ./.;

          # Add all necessary build tools
          nativeBuildInputs = [
            pkgs.sbt
            pkgs.jdk
            pkgs.nodejs_20
            pkgs.which # Keep which just in case for future debugging needs
          ];

          # Build steps
          buildPhase = ''
            runHook preBuild

            echo "Setting up build environment..."
            # Link the pre-built node_modules into the build directory
            ln -s ${npmDeps}/lib/node_modules node_modules

            # Add the node_modules/.bin directory to the PATH
            export PATH="$PWD/node_modules/.bin:$PATH"

            # Create a writable HOME directory AND subdirs SBT needs
            export HOME=$(mktemp -d)
            export SBT_HOME="$HOME/.sbt"
            export IVY2_HOME="$HOME/.ivy2"
            mkdir -p "$SBT_HOME/boot" "$IVY2_HOME"

            # Set SBT_OPTS environment variable to configure SBT/Ivy/User dirs
            export SBT_OPTS="-Dsbt.boot.directory=$SBT_HOME/boot -Dsbt.ivy.home=$IVY2_HOME -Duser.home=$HOME"

            # 1. Compile ScalaJS code
            echo "Running sbt fullLinkJS..."
            sbt fullLinkJS || {
              echo "ERROR: 'sbt fullLinkJS' failed!"
              exit 1
            }

            # 2. Run webpack using the absolute path
            echo "Running webpack using absolute path..."
            if [ -e "$PWD/node_modules/.bin/webpack" ]; then
              "$PWD/node_modules/.bin/webpack" --config webpack.prod.js --progress --color || {
                echo "ERROR: Direct webpack command failed!"
                exit 1
              }
            else
               echo "ERROR: $PWD/node_modules/.bin/webpack not found after SBT!"
               exit 1
            fi

            # Verify dist directory was created
            if [ ! -d "dist" ]; then
              echo "ERROR: dist directory not created after webpack execution"
              exit 1
            fi

            echo "Build phase completed."
            runHook postBuild
          '';

          # Standard install phase
          installPhase = ''
            runHook preInstall
            echo "Installing..."
            # Basic check before copying
            if [ ! -d "dist" ] || [ -z "$(ls -A dist)" ]; then
              echo "ERROR: dist directory not found or empty during installPhase"
              exit 1
            fi
            mkdir -p $out
            cp -r dist/* $out/
            echo "Installation finished."
            runHook postInstall
          '';

          # Disable checks that might interfere
          dontStrip = true;
          dontPatchShebangs = true;
        };

        # NixOS module definition (unchanged)
        nixosModule = { config, lib, pkgs, ... }: {
          options.rbtv-package = lib.mkOption {
            type = lib.types.package;
            description = "redblack tree visualization";
            default = self.packages.${system}.default;
          };
        };

        # Development Shell definition
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            sbt
            jdk
            nodejs_20
            # Add npmDeps here so the node_modules are available for linking
            npmDeps
          ];

          shellHook = ''
            echo "Entering development environment..."
            # Link pre-built node_modules if not present
            if [ ! -L "node_modules" ] || [ ! -d "node_modules" ]; then
              echo "Linking pre-built node_modules..."
              ln -sf ${npmDeps}/lib/node_modules node_modules
            fi
            # Add node_modules/.bin to PATH for development use
            export PATH="$PWD/node_modules/.bin:$PATH"
            echo "Node modules linked. PATH updated."
            echo "To build, run: sbt fullLinkJS && ./node_modules/.bin/webpack --config webpack.prod.js"
          '';
        };
      });
}
