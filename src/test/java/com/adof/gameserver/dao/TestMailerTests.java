//package com.muks.employee.emailer;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
///**
// * Created by 15692 on 23/06/16.
// */
//
//@Test
////@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
//public class TestMailerTests extends AbstractTestNGSpringContextTests {
//
//    @Autowired
//    EmailGenerator emailGenerator;
//
//    @Test()
//    void testEmailGenerator() {
//
//        String email = emailGenerator.generate();
//        System.out.println(email);
//
//        Assert.assertNotNull(email);
//        Assert.assertEquals(email, "feedback@yoursite.com");
//
//
//    }
//
//}