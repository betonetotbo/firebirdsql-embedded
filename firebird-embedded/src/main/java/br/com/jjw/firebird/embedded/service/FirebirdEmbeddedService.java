package br.com.jjw.firebird.embedded.service;

import java.io.IOException;
import java.nio.file.Path;

public interface FirebirdEmbeddedService {

	/**
	 * Retorna a versão do Firebird que será instalada.
	 * 
	 * @return A versão do firebird.
	 */
	String getVersion();

	/**
	 * Faz a instalação dos arquivos do Firebird no diretório informado.
	 * 
	 * @param destination
	 *            O diretório a serem instalados os arquivos do Firebird.
	 * @return Se a instalação foi realizada ({@code true}) ou se já havia uma instação do diretório informado ({@code false}).
	 */
	boolean install(Path destination) throws IOException;

}
