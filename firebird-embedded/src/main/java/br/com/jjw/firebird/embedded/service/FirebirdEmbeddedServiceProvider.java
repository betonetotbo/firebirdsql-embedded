package br.com.jjw.firebird.embedded.service;

import java.util.ServiceLoader;

public class FirebirdEmbeddedServiceProvider {

	private static ServiceLoader<FirebirdEmbeddedService> loader;

	public static synchronized FirebirdEmbeddedService getInstance() {
		if (loader == null) {
			loader = ServiceLoader.load(FirebirdEmbeddedService.class);
		}
		return loader.iterator().next();
	}

}
