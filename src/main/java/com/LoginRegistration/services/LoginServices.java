package com.LoginRegistration.services;

import java.util.Base64;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.LoginRegistration.Exception.UserNotFoundException;
import com.LoginRegistration.Repository.LoginRepository;
import com.LoginRegistration.Repository.RegisterRepository;
import com.LoginRegistration.entity.Password;
import com.LoginRegistration.entity.Login;
import com.LoginRegistration.entity.Profile;
import com.LoginRegistration.entity.Register;

@Service
public class LoginServices {

	@Autowired
	private LoginRepository loginRepository;

	@Autowired
	private RegisterRepository registerRepository;

	public Optional<Login> getUserDetails(String username, String password) throws UserNotFoundException {
		String encryptedpassword = getEncodedString(password);
		return loginRepository.findByUsernameAndPassword(username, encryptedpassword);

	}

	public static String getEncodedString(String password) {
		String encrytedpassword = Base64.getEncoder().encodeToString(password.getBytes());
		return encrytedpassword;
	}

	public ResponseEntity<String> getIdAndAns(Password fp) throws UserNotFoundException {

		int userid = loginRepository.findByUsername(fp.getUsername()).get().getUserid();
		Register register = registerRepository.findById(userid);
		Register security = new Register();
		security.setSecurity_id(register.getSecurity_id());
		security.setSecurity_answer(register.getSecurity_answer());
		if (register.getSecurity_id() == fp.getSecurity_id()
				&& register.getSecurity_answer().toString().equalsIgnoreCase(fp.getSecurity_answer()))
			return new ResponseEntity<String>("Credentials are valid, you can change password now!", HttpStatus.OK);
		return new ResponseEntity<String>("Please provide valid question and answer!", HttpStatus.BAD_REQUEST);
	}

	@Transactional
	public ResponseEntity<String> setpassword(Login login) {
		Optional<Login> logins = this.loginRepository.findByUsername(login.getUsername());
		String encryptedpassword = getEncodedString(login.getPassword());
		System.out.println("encryptedpassword = " + encryptedpassword);
		if (!logins.get().getPassword().toString().equalsIgnoreCase(encryptedpassword)) {
			loginRepository.updatePassword(login.getUsername(), encryptedpassword);
			return new ResponseEntity<String>("Password updated succesfully", HttpStatus.OK);
		}
		return new ResponseEntity<String>("Password entered already exist!", HttpStatus.OK);

	}

	public ResponseEntity<String> getsecurityIdAndAns(Password fp) {
		int userid = loginRepository.findByUsername(fp.getUsername()).get().getUserid();
		Register register = registerRepository.findById(userid);
		Register security = new Register();
		System.out.println("id is" + register.getSecurity_id());
		System.out.println("ans is" + register.getSecurity_answer());
		security.setSecurity_id(register.getSecurity_id());
		security.setSecurity_answer(register.getSecurity_answer());
		if (register.getSecurity_id() == fp.getSecurity_id()
				&& register.getSecurity_answer().toString().equalsIgnoreCase(fp.getSecurity_answer()))
			return new ResponseEntity<String>("Credentials are valid, you can change password now!", HttpStatus.OK);
		return new ResponseEntity<String>("Please provide valid question and answer!", HttpStatus.BAD_REQUEST);
	}

}
