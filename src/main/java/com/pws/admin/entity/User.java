package com.pws.admin.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.pws.admin.utility.AuditModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
public class User extends AuditModel implements UserDetails, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "first_name", length = 50,nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 50,nullable = false)
	private String lastName;

	@Column(name="dob",nullable = false)
	private Date dateOfBirth;

	@Column(name = "email", length = 100,nullable = false, unique = true)
	private String email;

	@Column(name = "phone_number",length = 12,nullable = false)
	private String phoneNumber;

	@Column(name = "password",nullable = false)
	private String password;

	@Column(name = "is_active", nullable = false)
	@ColumnDefault("TRUE")
	private Boolean isActive;


	@Column(name = "reset_password_otp")
	private Integer resetPasswordOtp;

	@Column(name = "reset_password_expiry")
	private LocalDateTime ResetPasswordExpiry;


	@Override
	public String getUsername() {
		return email;
	}
	

	@Override
	public String getPassword() {
		return password;
	}
	

	public int getUId() {
		return id;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isActive;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}




//	@Override
//	public String getPassword() {
//		  byte[] decordedValue = Base64.getDecoder().decode(password);;
//			return decordedValue.toString();
//	}
}
