//package org.example.teamspark.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.ses.SesClient;
//
//@Configuration
//public class AwsSesConfig {
//    Region region = Region.AP_NORTHEAST_1;
//
//    @Bean
//    public SesClient sesClient() {
//        return SesClient.builder()
//                .region(region)  // Set the desired region
//                .build();
//    }
//}
