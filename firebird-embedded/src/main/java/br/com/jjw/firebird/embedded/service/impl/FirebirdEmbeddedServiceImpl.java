package br.com.jjw.firebird.embedded.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import br.com.jjw.firebird.embedded.service.FirebirdEmbeddedService;

public class FirebirdEmbeddedServiceImpl implements FirebirdEmbeddedService {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public String getVersion() {
		Properties props = new Properties();
		try (InputStream in = getClass().getResourceAsStream("/firebird-embedded/version.properties")) {
			props.load(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return props.getProperty("version");
	}

	@Override
	public boolean install(Path destination) throws IOException {
		logger.info("Iniciando a instalação do Firebird Embedded");

		boolean filesExtracted = false;

		String md5 = getMd5();
		if (canUnzip(md5, destination)) {
			unzip(destination);
			Files.write(destination.resolve(".md5"), md5.getBytes(Charset.forName("ISO8859-1")));
			filesExtracted = true;
		}

		String path = System.getProperty("jna.library.path");
		if (path != null && !path.trim().isEmpty()) {
			path += ";";
		} else {
			path = "";
		}

		if (!isWindows()) {
			// no linux ficam aqui os .so
			destination = destination.resolve("lib");
		}
		path = path + destination.toAbsolutePath().toString();

		logger.info("Atualizando o jna.library.path para: " + path);
		System.setProperty("jna.library.path", path);

		logger.info("Instalação concluída");

		return filesExtracted;
	}

	private InputStream getResourceZip() {
		final String resource;
		if (isWindows()) {
			if (isArch64()) {
				resource = "/firebird-embedded/win64.zip";
			} else {
				resource = "/firebird-embedded/win32.zip";
			}
		} else {
			if (isArch64()) {
				resource = "/firebird-embedded/linux64.zip";
			} else {
				resource = "/firebird-embedded/linux32.zip";
			}
		}
		return getClass().getResourceAsStream(resource);
	}

	private boolean canUnzip(String md5, Path destination) throws IOException {
		logger.info("Verificando se é necessário fazer a extração dos arquivos do Firebird");

		Path cached = destination.resolve(".md5");
		if (Files.notExists(cached)) {
			logger.info("A versão da instalação do Firebird local não existe");
			return true;
		}

		List<String> lines = Files.readAllLines(cached, Charset.forName("ISO8859-1"));
		if (lines.isEmpty()) {
			logger.info("A versão da instalação do Firebird local não pode ser carregada");
			return true;
		}
		String localMd5 = lines.get(0);
		if (!localMd5.equals(md5)) {
			logger.info("A versão da instalação do Firebird local (" + localMd5 + ") é diferente da embarcada (" + md5 + ")");
			return true;
		}
		logger.info("A versão da instalação do Firebird local é a mesma que a embarcada: " + md5);
		return false;
	}

	private String getMd5() throws IOException {
		try (InputStream in = getResourceZip()) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			try (DigestInputStream dis = new DigestInputStream(in, md)) {
				byte[] buff = new byte[4096];
				while (dis.available() > 0) {
					dis.read(buff);
				}

				String fx = "%0" + (md.getDigestLength() * 2) + "x";
				return String.format(fx, new BigInteger(1, md.digest()));
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private void unzip(Path destination) throws IOException {
		logger.info("Descompactando os arquivos do Firebird");

		if (Files.exists(destination) && Files.isDirectory(destination)) {
			logger.fine("Excluindo o diretório: " + destination);
			Files.walk(destination).map(Path::toFile).sorted((o1, o2) -> -o1.compareTo(o2)).forEach(File::delete);
		}
		Files.createDirectories(destination);

		boolean linux = !isWindows();
		try (ZipInputStream zip = new ZipInputStream(getResourceZip())) {
			ZipEntry e;
			while ((e = zip.getNextEntry()) != null) {
				Path file = destination.resolve(e.getName());
				if (e.isDirectory()) {
					if (!Files.exists(file) || !Files.isDirectory(file)) {
						Files.createDirectories(file);
					}
				} else {
					logger.fine("Descompactando: " + file);
					Files.copy(zip, file, StandardCopyOption.REPLACE_EXISTING);
					// no linux temos que criar um link simbolico pra o lib original
					if (linux && file.getFileName().toString().startsWith("libfbclient.so.")) {
						logger.fine("Criando links simbólicos das bibliotecas do firebird");
						Files.createSymbolicLink(file.getParent().resolve("libfbclient.so"), file.getFileName());
						Files.createSymbolicLink(file.getParent().resolve("libfbclient.so.2"), file.getFileName());
						Files.createSymbolicLink(file.getParent().resolve("libfbembed.so"), file.getFileName());
					}
				}
			}
		}
	}

	private boolean isArch64() {
		return "64".equals(System.getProperty("sun.arch.data.model"));
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

}
