package com.example.services;

import com.example.FooterRequest;
import com.example.FooterResponse;
import com.example.FooterSectionServiceGrpc;
import com.example.entity.FooterSection;
import com.example.entity.UserEntity;
import com.example.repository.FooterSectionRepository;
import com.example.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@GrpcService
public class FooterSectionServiceImpl extends FooterSectionServiceGrpc.FooterSectionServiceImplBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private FooterSectionRepository footerSectionRepository;

    @Override
    @Transactional
    public void updateFooterSection(FooterRequest request, StreamObserver<FooterResponse> responseObserver) {
        try {
            // Find the user by ID
            UserEntity existingUser = userRepository.findById(request.getId()).orElse(null);

            if (existingUser == null) {
                // Handle the case when the user is not found
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException());
                return;
            }

            // Find the FooterSection for the user
            FooterSection existingFooterSection = footerSectionRepository.findByUser(existingUser).orElse(null);

            if (existingFooterSection == null) {
                // User is logging in for the first time, create a new FooterSection record with default values.
                FooterSection defaultFooterSection = createDefaultFooterSection(existingUser);
                footerSectionRepository.save(defaultFooterSection);

                // Convert FooterSection to FooterResponse
                FooterResponse response = convertToFooterResponse(defaultFooterSection);

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                // User is updating the FooterSection

                // Check if the request contains a basic value, if not, keep the existing value
                String newBasic = (request.getBasic() != null && !request.getBasic().isEmpty()) ? request.getBasic() : existingFooterSection.getBasic();
                // Check if the request contains a centered value, if not, keep the existing value
                String newCentered = (request.getCentered() != null && !request.getCentered().isEmpty()) ? request.getCentered() : existingFooterSection.getCentered();
                String newActive = (request.getActive() != null && !request.getActive().isEmpty()) ? request.getActive() : existingFooterSection.getActive();

                // Update the existing FooterSection with new or existing values
                existingFooterSection.setBasic(newBasic);
                existingFooterSection.setCentered(newCentered);
                existingFooterSection.setActive(newActive);

                footerSectionRepository.update(existingFooterSection);

                // Convert FooterSection to FooterResponse
                FooterResponse response = convertToFooterResponse(existingFooterSection);

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            // Handle exceptions and provide appropriate gRPC error status
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private FooterSection createDefaultFooterSection(UserEntity user) {
        FooterSection defaultFooterSection = new FooterSection();
        defaultFooterSection.setUser(user);
        defaultFooterSection.setBasic("{\"type\":\"div\",\"props\":{\"id\":\"footer-section\",\"children\":{\"type\":\"div\",\"props\":{\"id\":\"footer-body\",\"style\":{\"alignItems\":\"center\",\"backgroundColor\":\"#F69E9E\",\"borderWidth\":\"1px\",\"display\":\"flex\",\"height\":\"4rem\",\"justifyContent\":\"space-between\",\"paddingLeft\":\"4rem\",\"paddingRight\":\"4rem\",\"position\":\"relative\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"alignItems\":\"center\",\"display\":\"flex\",\"gap\":\"2.5rem\",\"justifyContent\":\"center\"},\"children\":[{\"type\":\"img\",\"props\":{\"alt\":\"\",\"src\":\"/src/assets/images/logo.webp\",\"style\":{\"height\":\"2.5rem\"}}},{\"type\":\"div\",\"props\":{\"children\":[{\"type\":\"span\",\"props\":{\"id\":\"footer-text\",\"style\":{\"cursor\":\"text\",\"color\":\"#000000\"},\"children\":[\"© Hamro Patro 2023, All Rights Reserved |\",\" \"]}},{\"type\":\"span\",\"props\":{\"id\":\"privacy\",\"style\":{\"cursor\":\"text\"},\"children\":\"Privacy\"}},\" \",\"|\",\" \",{\"type\":\"span\",\"props\":{\"id\":\"terms-of-service\",\"style\":{\"cursor\":\"text\"},\"children\":\"Terms of Service\"}}]}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"gap\":\"1.75rem\"},\"children\":[{\"type\":\"a\",\"props\":{\"id\":\"facebook\",\"href\":\"https://www.facebook.com/HamroPatro/\",\"style\":{\":hover\":{\"--transform-scale-x\":\"1.05\",\"--transform-scale-y\":\"1.05\"},\"borderRadius\":\"9999px\",\"color\":\"#1E40AF\",\"cursor\":\"pointer\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\"},\"children\":{\"type\":\"FaFacebook\",\"props\":{\"size\":25}}}},{\"type\":\"p\",\"props\":{\"style\":{\":hover\":{\"--transform-scale-x\":\"1.05\",\"--transform-scale-y\":\"1.05\"},\"borderRadius\":\"9999px\",\"cursor\":\"pointer\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\"},\"children\":{\"type\":\"RiInstagramFill\",\"props\":{\"size\":27}}}},{\"type\":\"p\",\"props\":{\"style\":{\":hover\":{\"--transform-scale-x\":\"1.05\",\"--transform-scale-y\":\"1.05\"},\"borderRadius\":\"9999px\",\"cursor\":\"pointer\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\"},\"children\":{\"type\":\"FaXTwitter\",\"props\":{\"size\":25}}}},{\"type\":\"p\",\"props\":{\"style\":{\":hover\":{\"--transform-scale-x\":\"1.05\",\"--transform-scale-y\":\"1.05\"},\"borderRadius\":\"9999px\",\"color\":\"#E50914\",\"cursor\":\"pointer\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\"},\"children\":{\"type\":\"IoLogoYoutube\",\"props\":{\"size\":26}}}}]}}]}}}}");
        defaultFooterSection.setCentered("{\"type\":\"div\",\"props\":{\"id\":\"footer-section\",\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"justifyContent\":\"space-between\",\"alignItems\":\"center\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingTop\":\"0.75rem\",\"paddingBottom\":\"0.75rem\",\"gap\":\"2.5rem\",\"justifyContent\":\"center\",\"alignItems\":\"center\",\"width\":\"100%\",\"height\":\"100%\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"p\",\"props\":{\"style\":{\"borderRadius\":\"9999px\",\"color\":\"#1E40AF\",\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"cursor\":\"pointer\",\":hover\":{\"--transform-scale-x\":\"1.1\",\"--transform-scale-y\":\"1.1\"}},\"children\":{\"type\":\"FaFacebook\",\"props\":{\"size\":25}}}},{\"type\":\"p\",\"props\":{\"style\":{\"borderRadius\":\"9999px\",\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"cursor\":\"pointer\",\":hover\":{\"--transform-scale-x\":\"1.05\",\"--transform-scale-y\":\"1.05\"}},\"children\":{\"type\":\"RiInstagramFill\",\"props\":{\"size\":27}}}},{\"type\":\"p\",\"props\":{\"style\":{\"borderRadius\":\"9999px\",\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"cursor\":\"pointer\",\":hover\":{\"--transform-scale-x\":\"1.05\",\"--transform-scale-y\":\"1.05\"}},\"children\":{\"type\":\"FaXTwitter\",\"props\":{\"size\":25}}}},{\"type\":\"p\",\"props\":{\"style\":{\"borderRadius\":\"9999px\",\"transitionProperty\":\"background-color, border-color, color, fill, stroke, opacity, box-shadow, transform\",\"transitionTimingFunction\":\"cubic-bezier(0.4, 0, 0.2, 1)\",\"transitionDuration\":[\"300ms\",\"200ms\"],\"cursor\":\"pointer\",\"color\":\"#E50914\",\":hover\":{\"--transform-scale-x\":\"1.05\",\"--transform-scale-y\":\"1.05\"}},\"children\":{\"type\":\"IoLogoYoutube\",\"props\":{\"size\":26}}}}]}},{\"type\":\"div\",\"props\":{\"id\":\"footer-body\",\"style\":{\"display\":\"flex\",\"paddingTop\":\"0.75rem\",\"paddingBottom\":\"0.75rem\",\"gap\":\"2.5rem\",\"justifyContent\":\"center\",\"alignItems\":\"center\",\"borderTopWidth\":\"1px\",\"borderColor\":\"#FFFFFF\",\"width\":\"100%\",\"height\":\"100%\",\"color\":\"#FFFFFF\",\"backgroundColor\":\"#DC2626\"},\"children\":{\"type\":\"p\",\"props\":{\"id\":\"footer-text\",\"style\":{\"color\":\"#000000\"},\"children\":[\"© Hamro Patro \",2023,\", All Rights Reserved |\",\" \",{\"type\":\"span\",\"props\":{\"style\":{\"cursor\":\"pointer\",\":hover\":{\"textDecoration\":\"underline\"}},\"children\":\"Privacy\"}},\" |\",\" \",{\"type\":\"span\",\"props\":{\"style\":{\"cursor\":\"pointer\",\":hover\":{\"textDecoration\":\"underline\"}},\"children\":\"Terms of Service\"}}]}}}}]}}}}");
        defaultFooterSection.setActive("Centered");
        return defaultFooterSection;
    }

    private FooterResponse convertToFooterResponse(FooterSection footerSection) {
        return FooterResponse.newBuilder()
                .setBasic(footerSection.getBasic())
                .setCentered(footerSection.getCentered())
                .setActive(footerSection.getActive())
                .build();
    }
}
