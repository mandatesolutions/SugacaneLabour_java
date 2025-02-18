package com.sugarcanelabour.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Document;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.LaborService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LaborServiceImpl implements LaborService {

	private static final String PDF_DIRECTORY = "E://New folder//Projects//PDF labor"; // Directory to store the
																						// generated PDFs

	private SupervisorDetailsRepository supervisorDetailsRepository;
	private DocumentRepository documentRepository;
	private CommonLoginRepository commonLoginRepository;
	private RedisTemplate<String, Object> redisTemplate;

	private SpringTemplateEngine templateEngine;

	private JavaMailSender javaMailSender;

	public LaborServiceImpl(SupervisorDetailsRepository supervisorDetailsRepository,
			DocumentRepository documentRepository, CommonLoginRepository commonLoginRepository,
			RedisTemplate<String, Object> redisTemplate, SpringTemplateEngine templateEngine,
			JavaMailSender javaMailSender) {
		super();
		this.supervisorDetailsRepository = supervisorDetailsRepository;
		this.documentRepository = documentRepository;
		this.commonLoginRepository = commonLoginRepository;
		this.redisTemplate = redisTemplate;
		this.templateEngine = templateEngine;
		this.javaMailSender = javaMailSender;
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(Long commonLoginId) {
		ApiResponse<Map<String, Object>> response = new ApiResponse<>();
		Map<String, Object> data = new HashMap<>();

		// Fetch labor details
		CommonLogin commonLogin = commonLoginRepository.findById(commonLoginId)
				.orElseThrow(() -> new RuntimeException("Labor not found"));

		// Check if the role is LABOR
		if (!"ROLE_LABOR".equalsIgnoreCase(commonLogin.getRole().getRoleName())) {
			response.setStatus("FAILED");
			response.setMessage("User is not a laborer.");
			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
		}

		// Fetch supervisor details (assuming a supervisor or labor-related entity
		// exists)
		Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository.findByCommonLogin(commonLogin);

		if (!supervisorDetailsOpt.isPresent()) {
			response.setStatus("FAILED");
			response.setMessage("Supervisor details not found.");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();

		// Collect labor details
		data.put("userId", commonLogin.getUserId());
		data.put("email", commonLogin.getEmail());
		data.put("role", commonLogin.getRole().getRoleName());
		data.put("mobileNo", commonLogin.getMobileNo());
		data.put("firstName", supervisorDetails.getFirstName());
		data.put("lastName", supervisorDetails.getLastName());
		data.put("gender", supervisorDetails.getGender());
		data.put("bloodGroup", supervisorDetails.getBloodGroup());
		data.put("address", supervisorDetails.getAddress());
		data.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
		data.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
		data.put("age", supervisorDetails.getAge());
		data.put("familyMembers", supervisorDetails.getFamilyMembers());
		data.put("medicalHistory", supervisorDetails.getMedicalHistory());
		data.put("uniqueLaborId", supervisorDetails.getUniqueLaborId());

		// Fetch documents associated with the labor
		List<Document> documents = documentRepository.findByCommonLogin(commonLogin);

		if (!documents.isEmpty()) {
			List<Map<String, String>> documentDetails = new ArrayList<>();

			for (Document document : documents) {
				Map<String, String> documentInfo = new HashMap<>();
				documentInfo.put("documentType", document.getDocumentType().name());
				documentInfo.put("documentLink", document.getDocumentLink());
				documentDetails.add(documentInfo);
			}

			data.put("documents", documentDetails);
		} else {
			data.put("documents", CommonMessages.N_D_U);
		}

		response.setStatus(CommonMessages.SUCCESS);
		response.setMessage(CommonMessages.L_DS);
		response.setData(data);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@Override
	@Transactional
	public ResponseEntity<Map<String, Object>> generateLaborDetailsPdfAndSendEmail(Long commonLoginId,
			String recipientEmail) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Step 1: Generate PDF
			CommonLogin commonLogin = commonLoginRepository.findById(commonLoginId)
					.orElseThrow(() -> new RuntimeException("Labor not found"));

			SupervisorDetails supervisorDetails = supervisorDetailsRepository.findByCommonLogin(commonLogin)
					.orElseThrow(() -> new RuntimeException("Supervisor details not found"));

			List<Document> documents = documentRepository.findByCommonLogin(commonLogin);

			Context context = new Context();
			context.setVariable("userId", commonLogin.getUserId());
			context.setVariable("email", commonLogin.getEmail());
			context.setVariable("role", commonLogin.getRole().getRoleName());
			context.setVariable("mobileNo", commonLogin.getMobileNo());
			context.setVariable("firstName", supervisorDetails.getFirstName());
			context.setVariable("lastName", supervisorDetails.getLastName());
			context.setVariable("gender", supervisorDetails.getGender());
			context.setVariable("bloodGroup", supervisorDetails.getBloodGroup());
			context.setVariable("address", supervisorDetails.getAddress());
			context.setVariable("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			context.setVariable("talukaId", supervisorDetails.getTaluka().getTalukaId());
			context.setVariable("age", supervisorDetails.getAge());
			context.setVariable("familyMembers", supervisorDetails.getFamilyMembers());
			context.setVariable("medicalHistory", supervisorDetails.getMedicalHistory());
			context.setVariable("uniqueLaborId", supervisorDetails.getUniqueLaborId());
			context.setVariable("documents", documents);

			String htmlContent = templateEngine.process("laborDetailsTemplate", context);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.useFastMode();
			builder.withHtmlContent(htmlContent, null);
			builder.toStream(outputStream);
			builder.run();

			byte[] pdfBytes = outputStream.toByteArray();

			Path filePath = Paths.get(PDF_DIRECTORY, "labor_" + commonLogin.getUserId() + ".pdf");
			Files.write(filePath, pdfBytes);

			// Step 2: Send Email with PDF Attachment
			sendEmailWithAttachment(recipientEmail, filePath);

			// Step 3: Return Success Response
			response.put("message", "PDF generated and sent via email successfully");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", "Failed to generate and send email");
			response.put("details", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// This method sends an email with the PDF download link
	private void sendEmailWithAttachment(String recipientEmail, Path filePath) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setFrom("vaishubakal883@gmail.com");
			helper.setTo(recipientEmail);
			helper.setSubject("Labor Details PDF");
			helper.setText("Attached is your labor details PDF.", true);

			// Attach the PDF
			helper.addAttachment("Labor_Details.pdf", filePath.toFile());

			// Send the email
			javaMailSender.send(mimeMessage);

			System.out.println("Email sent successfully with PDF attachment.");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}


}
