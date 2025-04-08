{
  description = "Redblack tree visualization";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    sbt-derivation.url = "github:zaninime/sbt-derivation";
    sbt-derivation.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, flake-utils, sbt-derivation }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
      in {
        packages.default = sbt-derivation.mkSbtDerivation.${system} {
          pname = "rbtv";
          version = "0.1.0";
          src = ./.;

          # Hash for the dependencies - will fail the first time with the correct hash
          # Replace with the hash from the error message
          depsSha256 = "sha256-HVYJvzWT3HBrq1a+sSPDf80L+xSXFnIBhzO0IvhkuHA=";
          
          # SBT command to build the site
          buildPhase = ''
            sbt pd
          '';

          # Copy the built site to the output
          installPhase = ''
            mv dist $out
          '';
        };

        # Make the package available as a NixOS module option
        nixosModule = { config, lib, pkgs, ... }: {
          options.rbtv-package = lib.mkOption {
            type = lib.types.package;
            description = "redblack tree visualization";
            default = self.packages.${pkgs.system}.default;
          };
        };

        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            sbt
            jdk
          ];
        };
      });
}
