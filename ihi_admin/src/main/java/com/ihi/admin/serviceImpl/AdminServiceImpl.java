package com.ihi.admin.serviceImpl;

import java.time.LocalDate;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihi.admin.exception.UserNameNotFoundException;
import com.ihi.admin.model.User;
import com.ihi.admin.mongo.model.HederaHtsInfo;
import com.ihi.admin.mongo.repository.HederaHtsInfoRepository;
import com.ihi.admin.mongo.repository.HederaSubscribeInfoRepo;
import com.ihi.admin.payload.request.GenerateInvoice;
import com.ihi.admin.payload.response.MessageResponse;
import com.ihi.admin.repository.UserRepository;
import com.ihi.admin.service.AdminService;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HederaHtsInfoRepository hederaHtsInfoRepository;

	@Autowired
	HederaSubscribeInfoRepo hederaSubscribeInfoRepo;

	@Autowired
	Environment env;

	@Override
	public ResponseEntity<?> generateInvoice(GenerateInvoice generateInvoice) {
		Optional<User> user = userRepository.findByUsername(generateInvoice.getClientId());
		if (!user.isPresent())
			throw new UserNameNotFoundException();

		LocalDate initial = LocalDate.of(generateInvoice.getYear(),generateInvoice.getMonth(), 1);
		LocalDate start = initial.withDayOfMonth(1);
		LocalDate end = initial.withDayOfMonth(initial.getMonth().length(initial.isLeapYear()));
		DateTime dtFrom = new DateTime(start + "T00:00:00");
		DateTime dtTo = new DateTime(end + "T23:59:00");  
		
		Pageable paging = PageRequest.of(generateInvoice.getPage(), generateInvoice.getSize());
		Page<HederaHtsInfo> list1 = hederaHtsInfoRepository.findAllByClientIdAndCreatedTimeBetween(generateInvoice.getClientId(),dtFrom,dtTo,paging);
		
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("fees.fetched.success")).response(list1).httpStatus(HttpStatus.OK).build());
	}

}
