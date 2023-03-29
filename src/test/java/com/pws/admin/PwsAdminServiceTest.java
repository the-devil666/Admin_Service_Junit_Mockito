package com.pws.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.pws.admin.PwsAdminServiceApplication;
import com.pws.admin.controller.AdminController;
import com.pws.admin.dto.PermissionDTO;
import com.pws.admin.dto.UserRoleXrefDTO;
import com.pws.admin.entity.*;
import com.pws.admin.entity.Module;
import com.pws.admin.repository.ModuleRepository;
import com.pws.admin.repository.PermissionRepository;
import com.pws.admin.repository.RoleRepository;
import com.pws.admin.repository.UserRepository;
import com.pws.admin.service.AdminService;
import com.pws.admin.utility.JwtUtil;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PwsAdminServiceApplication.class)
@SpringBootTest
public class PwsAdminServiceTest {


	@Configuration
	public class MockMvcConfig {
		@Autowired
		private WebApplicationContext webApplicationContext;

		@Bean
		public MockMvc mockMvc() {
			return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		}
	}

	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;

	ObjectMapper om = new ObjectMapper();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private UserRepository userRepository;


	@InjectMocks
	private AdminController adminController;

	@Mock
	private AdminService adminService;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private ModuleRepository moduleRepository;

	@Mock
	private PermissionRepository permissionRepository;

	@Test
	public void testAddRole() throws Exception {
		Role role = new Role();
		role.setId(1);
		role.setName("CRO");
		role.setIsActive(true);

		doNothing().when(adminService).addRole(role);

		mockMvc.perform(post("http://localhost:8082/admin/private/role/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(role)))
				.andExpect(status().isOk());
		System.out.println("Role Added Successfully");

	}


	@Test
	public void testUpdateRole() throws Exception {
		// Create a test role
		Role testRole = new Role();
		testRole.setId(1);
		testRole.setName("testRole");

		// Set up the MockMvc and send a PUT request with the test role
		mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
		mockMvc.perform(put("http://localhost:8082/admin/private/role/update")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(testRole)))
				.andExpect(status().isOk());
		System.out.println("Role Updated successfully");

		// Verify that the AdminService's updateRole method was called with the test role
		verify(adminService).updateRole(testRole);
	}


	@Test
	public void testFetchRoleById() throws Exception {
		Role role = new Role();
		role.setId(1);
		role.setName("CRO");
		role.setIsActive(true);
adminService.addRole(role);
		when(roleRepository.findById(1)).thenReturn(Optional.of(role));
		System.out.println(roleRepository.findById(1));
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8082/admin/private/role/fetch/by/id?id=1"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(role.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(role.getName()));
		System.out.println("Role fetched Successfully");
	}


	@Test
	public void testFetchAllRole() throws Exception {
		//Arrange
		List<Role> roles = new ArrayList<>();
		Role role1 = new Role();
		role1.setId(1);
		role1.setName("CRO");
		role1.setIsActive(true);
		roles.add(role1);
		given(adminService.fetchAllRole()).willReturn(roles);

		//Act
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8082/admin/private/role/fetch/all"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("CRO"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isActive").value(true))
				.andReturn();


		//Assert
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		String expectedResponseBody = "{\"status\":\"OK\",\"data\":[{\"id\":1,\"name\":\"CRO\",\"isActive\":true}]}";
		JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, false);
		System.out.println("All Role Fetched successfully");
	}


	@Test
	public void testActivateOrDeactivateRole() throws Exception {
		Role role = new Role();
		role.setId(1);
		role.setIsActive(true);

		doNothing().when(adminService).deactivateOrActivateRoleById(1,true);

		mockMvc.perform(post("http://localhost:8082/admin/private/role/activate/deactivate?id=1&flag=true")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(role)))
				.andExpect(status().isOk());
		System.out.println("Operation completed Successfully");

	}


	@Test
	public void testAddModule() throws Exception {
		Module module = new Module();
		module.setName("Dashboard");
		module.setIsActive(true);
(adminService).addModule(module);

		mockMvc.perform(post("http://localhost:8082/admin/private/module/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(module)))
				.andExpect(status().isOk());
		System.out.println("Module Added Successfully");

	}


	@Test
	public void testUpdateModule() throws Exception {
		// Create a test role
		Module module = new Module();
		module.setId(1);
		module.setName("Dashboard");
		module.setIsActive(true);

		// Set up the MockMvc and send a PUT request with the test role
		mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
		mockMvc.perform(put("http://localhost:8082/admin/private/module/update")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(module)))
				.andExpect(status().isOk());
		System.out.println("Module Updated successfully");

		// Verify that the AdminService's updateRole method was called with the test role
		verify(adminService).updateModule(module);
	}


	@Test
	public void testFetchModuleById() throws Exception {
		Module module = new Module();
		module.setId(1);
		module.setName("Dashboard");
		module.setIsActive(true);
moduleRepository.save(module);
		when(moduleRepository.findById(1)).thenReturn(Optional.of(module));
//		System.out.println(adminService.fetchModuleById(1));
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8082/admin/private/module/fetch/id").param("id","1"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(module.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(module.getName()));
		System.out.println("Module fetched Successfully");
	}


	@Test
	public void testFetchAllModule() throws Exception {
		List<Module> modules = new ArrayList<>();

		//Arrange

//		modules.add(module1);
		given(moduleRepository.findAll()).willReturn(modules);

		//Act
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8082/admin/private/module/fetchall"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Dashboard"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isActive").value(true))

				.andReturn();


		//Assert
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		String expectedResponseBody = "{\"status\":\"OK\",\"data\":[{\"id\":1,\"name\":\"Dashboard\",\"isActive\":true}]}";
		JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, false);
		System.out.println("All Module Fetched successfully");
	}


	@Test
	public void testActivateOrDeactivateModule() throws Exception {
		Module module = new Module();
		module.setId(1);
		module.setIsActive(true);

		doNothing().when(adminService).deactivateOrActivateModuleById(1,true);

		mockMvc.perform(post("http://localhost:8082/admin/private/module/activate/inactivate?id=1&flag=true")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(module)))
				.andExpect(status().isOk());
		System.out.println("Operation completed Successfully");

	}


	@Test
	public void testSaveOrUpdateUserRoleXref() throws Exception {
		// Create a test role
		UserRoleXrefDTO userRoleXrefDTO = new UserRoleXrefDTO();
		userRoleXrefDTO.setId(1);
		userRoleXrefDTO.setUserId(1);
		userRoleXrefDTO.setRoleId(1);

		// Set up the MockMvc and send a PUT request with the test role
		mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
		mockMvc.perform(post("http://localhost:8082/admin/private/save/update/userxref")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(userRoleXrefDTO)))
				.andExpect(status().isOk());
		System.out.println("UserXref Updated successfully");

		// Verify that the AdminService's updateRole method was called with the test role
		verify(adminService).saveOrUpdateUserXref(userRoleXrefDTO);
	}


	@Test
	public void testDeactivateOrActivateAssignedRoleToUser() throws Exception {
		UserRoleXrefDTO userRoleXrefDTO = new UserRoleXrefDTO();
		userRoleXrefDTO.setId(1);
		userRoleXrefDTO.setIsActive(true);
		doNothing().when(adminService).deactivateOrActivateAssignedRoleToUser(1,true);

		mockMvc.perform(post("http://localhost:8082/admin/private/userxref/activate/deactivate/byuser?id=1&flag=true")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(userRoleXrefDTO)))
				.andExpect(status().isOk());
		System.out.println("Test Operation completed Successfully");

	}


	@Test
	public void testFetchUserById() throws Exception {
		UserRoleXref userRoleXref = new UserRoleXref();
		User user = new User(1, "Kiran", "R", new Date(1999, 10, 13), "kiranraj13101999@gmail.com", "9019716868", "Kiran@123", true, null, null);
		userRoleXref.setId(1);
		userRoleXref.setUser(user);

		when(adminService.fetchUserById(1)).thenReturn(Optional.of(userRoleXref));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8082/admin/private/fetch/fetchUserById")
						.param("Id", "1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String responseContent = result.getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseContent);

		assertEquals("OK", jsonNode.get("status").asText());
//		assertEquals(1, jsonNode.get("data").get("id").asInt());
//		assertEquals(user.getEmail(), jsonNode.get("data").get("user").get("email").asText());

		System.out.println("User fetched successfully");
	}

	@Test
	public void testAddPermission() throws Exception {
		Role role= new Role(1,"CEO",true);
		adminService.addRole(role);
		Module module= new Module(1,true,"Dashboard");
		adminService.addModule(module);
		PermissionDTO permission = new PermissionDTO();
		permission.setId(1);
		permission.setIsAdd(true);
		permission.setIsView(true);
		permission.setIsActive(true);
		permission.setIsUpdate(true);
		permission.setIsDelete(false);
		permission.setRole(1);
		permission.setModule(1);

		doNothing().when(adminService).addPermission(permission);

		mockMvc.perform(post("http://localhost:8082/admin/private/permmision/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(permission)))
				.andExpect(status().isOk());
		System.out.println("Permission Added Successfully");

	}


	@Test
	public void testUpdatePermission() throws Exception {
		// Create a test role
		Role role= new Role(1,"CEO",true);
		adminService.addRole(role);
		Module module= new Module(1,true,"Dashboard");
		adminService.addModule(module);
		PermissionDTO permission = new PermissionDTO();
		permission.setId(1);
		permission.setIsAdd(false);
		permission.setIsView(true);
		permission.setIsActive(true);
		permission.setIsUpdate(false);
		permission.setIsDelete(false);
		permission.setRole(1);
		permission.setModule(1);


		// Set up the MockMvc and send a PUT request with the test role
		mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
		mockMvc.perform(put("http://localhost:8082/admin/private/permmision/update")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(permission)))
				.andExpect(status().isOk());
		System.out.println("Permission Updated successfully");

		// Verify that the AdminService's updateRole method was called with the test role
		verify(adminService).updatePermission(permission);
	}




	@Test
	public void testFetchAllPermission() throws Exception {
		List<Permission> permissions = new ArrayList<>();
//		Role role= new Role(1,"CEO",true);
//		Module module= new Module(1,true,"Dashboard");

		//Arrange

//		modules.add(module1);
		given(permissionRepository.findAll()).willReturn(permissions);

		//Act
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8082/admin/private/permission/fetchall"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isAdd").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isView").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isActive").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isUpdate").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].isDelete").value(false))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].role").value(null))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data[0].module").value("null"))

				.andReturn();


		//Assert
		String actualResponseBody = mvcResult.getResponse().getContentAsString();
		String expectedResponseBody = "{\"status\":\"OK\",\"data\":[{\"id\":1,\"isAdd\":\"true\",\"isView\":true,\"isActive\":true,\"isUpdate\":true,\"isDelete\":false,\"role\":null,\"module\":null}]}";
		JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, false);
		System.out.println("All Permission Fetched successfully");
	}





	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}




}