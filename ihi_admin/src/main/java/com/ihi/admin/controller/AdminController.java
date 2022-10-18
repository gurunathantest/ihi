package com.ihi.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ihi.admin.payload.request.GenerateInvoice;
import com.ihi.admin.service.AdminService;


@Controller
@RequestMapping(name = "/api/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@PostMapping("/generate/invoice")
	public ResponseEntity<?> generateInvoice(@RequestBody GenerateInvoice generateInvoice) {
		return adminService.generateInvoice(generateInvoice);
	}

}
