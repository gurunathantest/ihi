package com.ihi.auth.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class RestTemplateService {

	public HttpHeaders headers(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		HttpHeaders headers = new HttpHeaders();
		String userAgent = "AdminTool";
		headers.set(HttpHeaders.USER_AGENT, userAgent);
		headers.add("Authorization", headerAuth);
		return headers;
	}

	public URI uri(String u) {
		try {
			return new URI(u);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
