
#!/usr/bin/env bash
########################################################################################################################
# -Name:
# -Description:
# -Creation Date:
# -Last Modified:
# -Author:
# -Email:
# -Permissions:
# -Args:
# -Usage:
# -License:
########################################################################################################################

main()
{
  createdb -E UTF8 --locale='en_US.utf8' -T template0 -O $(whoami) "eChempad"

}

set -e
main "$@"