package com.devbreeze.learn.httpclient;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpClientTest {

	private HttpServer server;

	private HttpHost host;

	private CloseableHttpClient client;

	@Before
	public void setUp() throws Exception {
		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(15000).build();
		ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap().setSocketConfig(socketConfig)
				.setServerInfo("TEST/1.1").registerHandler("/helloworld", new HttpRequestHandler() {

					@Override
					public void handle(HttpRequest request, HttpResponse response, HttpContext context)
							throws HttpException, IOException {
						response.setStatusCode(HttpStatus.SC_OK);
						response.setEntity(new StringEntity("Hello, World!"));
					}
				}).registerHandler("/hellovariable/*", new HttpRequestHandler() {

					@Override
					public void handle(HttpRequest request, HttpResponse response, HttpContext context)
							throws HttpException, IOException {
						response.setStatusCode(HttpStatus.SC_OK);
						response.setEntity(new StringEntity("Hello, " + request.getRequestLine().getUri().substring(15)
								+ "!"));
					}
				});
		server = serverBootstrap.create();
		server.start();
		host = new HttpHost("localhost", server.getLocalPort());

		HttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultSocketConfig(socketConfig)
				.setConnectionManager(connectionManager);
		client = clientBuilder.build();
	}

	@After
	public void shutDown() throws Exception {
		client.close();
		server.shutdown(10, TimeUnit.SECONDS);
	}

	@Test
	public void testHelloWorld() throws Exception {
		HttpGet get = new HttpGet("http://localhost/helloworld");
		CloseableHttpResponse response = client.execute(host, get);
		try {
			assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
			assertEquals("Hello, World!", EntityUtils.toString(response.getEntity()));
		} finally {
			response.close();
		}
	}

	@Test
	public void testHelloVariable() throws Exception {
		HttpGet get = new HttpGet("http://localhost/hellovariable/Foo");
		CloseableHttpResponse response = client.execute(host, get);
		try {
			assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
			assertEquals("Hello, Foo!", EntityUtils.toString(response.getEntity()));
		} finally {
			response.close();
		}
	}
}
