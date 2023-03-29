import com.pws.admin.controller.AdminController;
import com.pws.admin.dto.SignUpDTO;
import com.pws.admin.exception.config.PWSException;
import com.pws.admin.service.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignUpControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    @Test
    public void testSignup() throws PWSException {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setFirstName("John");
        signUpDTO.setLastName("Doe");
        signUpDTO.setEmail("johndoe@example.com");
        signUpDTO.setPassword("P@ssw0rd");
        signUpDTO.setPhoneNumber("1234567890");
        signUpDTO.setDateOfBirth(new Date(1990, 1, 1));
        signUpDTO.setOtp("123456");

        // Mock the behavior of the adminService
        doNothing().when(adminService).UserSignUp(signUpDTO);

        // Call the signup method in the adminController
        ResponseEntity<Object> responseEntity = adminController.signup(signUpDTO);

        // Verify that the adminService's UserSignUp method was called once
        verify(adminService, times(1)).UserSignUp(signUpDTO);

        // Verify that the response entity's status code is HttpStatus.OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        System.out.println("signed up successfully");
    }

    @Test(expected = PWSException.class)
    public void testSignupWithInvalidPassword() throws PWSException {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setFirstName("John");
        signUpDTO.setLastName("Doe");
        signUpDTO.setEmail("johndoe@example.com");
        signUpDTO.setPassword("password"); // invalid password
        signUpDTO.setPhoneNumber("1234567890");
        signUpDTO.setDateOfBirth(new Date(1990, 1, 1));
        signUpDTO.setOtp("123456");

        doThrow(new PWSException("Invalid Password")).when(adminService).UserSignUp(signUpDTO);

        try {
            adminController.signup(signUpDTO);
        } catch (PWSException e) {
            assertEquals("Invalid Password", e.getMessage());
            System.out.println("Test Passed for invalid password");
            throw e;
        }

        System.out.println("Test Failed for invalid password - expected PWSException to be thrown");
    }



    @Test(expected = PWSException.class)
    public void testSignupWithExistingEmail() throws PWSException {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setFirstName("John");
        signUpDTO.setLastName("Doe");
        signUpDTO.setEmail("johndoe@example.com"); // existing email
        signUpDTO.setPassword("P@ssw0rd");
        signUpDTO.setPhoneNumber("1234567890");
        signUpDTO.setDateOfBirth(new Date(1990, 1, 1));
        signUpDTO.setOtp("123456");

        doThrow(new PWSException("User Already Exist with Email : " + signUpDTO.getEmail())).when(adminService).UserSignUp(signUpDTO);

        adminController.signup(signUpDTO);
        System.out.println("Test Passed for exiting email");

    }

    @Test(expected = PWSException.class)
    public void testSignupWithInvalidOTP() throws PWSException {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setFirstName("John");
        signUpDTO.setLastName("Doe");
        signUpDTO.setEmail("johndoe@example.com");
        signUpDTO.setPassword("P@ssw0rd");
        signUpDTO.setPhoneNumber("1234567890");
        signUpDTO.setDateOfBirth(new Date(1990, 1, 1));
        signUpDTO.setOtp("invalid"); // invalid OTP

        doThrow(new PWSException("Invalid OTP")).when(adminService).UserSignUp(signUpDTO);

        adminController.signup(signUpDTO);
        System.out.println("Test Passed for invalid OTP");

    }
}
