{
  description = "Redblack tree visualization";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    # sbt-derivation is only needed if you build SBT deps separately, which we aren't here
    # sbt-derivation.url = "github:zaninime/sbt-derivation";
    # sbt-derivation.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, flake-utils }: # Removed sbt-derivation from inputs
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

          # Install node_modules into a lib subdirectory for clarity
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
            pkgs.which # Explicitly add 'which' for debugging PATH
          ];

          # Build steps
          buildPhase = ''
            runHook preBuild # Run standard hooks if any

            echo "--- Running buildPhase ---"
            # Link the pre-built node_modules into the build directory
            ln -s ${npmDeps}/lib/node_modules node_modules

            # Add the node_modules/.bin directory to the PATH
            export PATH="$PWD/node_modules/.bin:$PATH"

            # Create a writable HOME directory AND subdirs SBT needs
            export HOME=$(mktemp -d)
            export SBT_HOME="$HOME/.sbt"
            export IVY2_HOME="$HOME/.ivy2"
            mkdir -p "$SBT_HOME/boot" "$IVY2_HOME" # Ensure boot dir exists too
            echo "Created temporary HOME: $HOME"

            # Debug: Check environment before SBT
            echo "Environment BEFORE SBT:"
            echo "PATH: $PATH"
            echo "HOME: $HOME"
            which webpack || echo "> webpack not found in PATH before SBT"
            ls -la "$PWD/node_modules/.bin/webpack" || echo "> Link node_modules/.bin/webpack not found before SBT"

            # 1. Compile ScalaJS code, setting SBT_OPTS to configure SBT/Ivy/User dirs
            echo "Running sbt fullLinkJS..."
            # Set SBT_OPTS environment variable
            export SBT_OPTS="-Dsbt.boot.directory=$SBT_HOME/boot -Dsbt.ivy.home=$IVY2_HOME -Duser.home=$HOME"
            echo "SBT_OPTS: $SBT_OPTS"

            # Run sbt (it should pick up SBT_OPTS)
            sbt fullLinkJS || {
              echo "ERROR: 'sbt fullLinkJS' failed!"
              # Add more debug info if it fails
              echo "Checking temporary HOME contents:"
              ls -la "$HOME"
              ls -la "$SBT_HOME"
              ls -la "$IVY2_HOME"
              exit 1
            }

            # Unset SBT_OPTS if needed for subsequent commands, though likely not necessary here
            # unset SBT_OPTS

            # Debug: Check environment after SBT
            echo "Environment AFTER SBT:"
            echo "PATH: $PATH" # Check if PATH is preserved
            which webpack || echo "> webpack not found in PATH after SBT"
            ls -la "$PWD/node_modules/.bin/webpack" || echo "> Link node_modules/.bin/webpack not found after SBT"

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

            # Check if dist directory was created
            if [ ! -d "dist" ]; then
              echo "ERROR: dist directory not created after webpack execution"
              ls -la
              exit 1
            fi
            echo "--- buildPhase finished successfully ---"

            runHook postBuild # Run standard hooks if any
          '';

          # Standard install phase
          installPhase = ''
            runHook preInstall
            echo "--- Running installPhase ---"
            if [ ! -d "dist" ] || [ -z "$(ls -A dist)" ]; then
              echo "ERROR: dist directory not found or empty during installPhase"
              exit 1
            fi
            mkdir -p $out
            cp -r dist/* $out/
            echo "--- installPhase finished ---"
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

        # Development Shell definition needs adjustment
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
              echo "Linking pre-built node_modules from ${npmDeps}/lib/node_modules..."
              # Use -f to force link creation/overwrite
              ln -sf ${npmDeps}/lib/node_modules node_modules
            fi
            # Add node_modules/.bin to PATH for development use
            export PATH="$PWD/node_modules/.bin:$PATH"
            echo "Node modules linked. PATH updated."
            echo "Run 'sbt fullLinkJS && ./node_modules/.bin/webpack --config webpack.prod.js' or 'sbt pd'"
          '';
        };
      });
}
