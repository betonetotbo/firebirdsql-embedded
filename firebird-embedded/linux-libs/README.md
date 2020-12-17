# Bibliotecas requeridas para linux

O firebird 3 necessita de 2 bibliotecas

- [ncurses5](https://packages.debian.org/buster/libncurses5)
- [tommath1](https://packages.debian.org/buster/libtommath1)
- [libtinfo5](https://packages.debian.org/buster/libtinfo5)

Para atualizar estas bibliotecas acesse os links acima, e baixe os arquivos .deb de cada arquitetura (i386 e amd64).
Descompacte-os e coloque-os nos respectivos diretórios.

Não se esqueça de remover os arquivos antigos e se os nomes dos arquivos foram alterados, atualize o `build.xml` que é o script responsável por gerar as distribuições.
Atualize também se necessário a classe `FirebirdEmbeddedServiceImpl` que é responsável por criar os links simbólicos dessas libs.