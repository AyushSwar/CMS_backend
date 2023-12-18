package com.example.services;

import com.example.NavRequest;
import com.example.NavResponse;
import com.example.NavSectionServiceGrpc;
import com.example.entity.NavSection;
import com.example.entity.UserEntity;
import com.example.repository.NavSectionRepository;
import com.example.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@GrpcService
public class NavSectionServiceImpl extends NavSectionServiceGrpc.NavSectionServiceImplBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private NavSectionRepository navSectionRepository;

    @Override
    @Transactional
    public void updateNavSection(NavRequest request, StreamObserver<NavResponse> responseObserver) {
        try {
            // Find the user by ID
            UserEntity existingUser = userRepository.findById(Long.parseLong(request.getId())).orElse(null);

            if (existingUser == null) {
                // Handle the case when the user is not found
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException());
                return;
            }

            // Find the NavSection for the user
            NavSection existingNavSection = navSectionRepository.findByUser(existingUser).orElse(null);

            if (existingNavSection == null) {
                // User is logging in for the first time, create a new NavSection record with default values.
                NavSection defaultNavSection = createDefaultNavSection(existingUser);
                navSectionRepository.save(defaultNavSection);

                // Convert NavSection to NavResponse
                NavResponse response = convertToNavResponse(defaultNavSection);

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                // User is updating the NavSection

                // Check if the request contains a title value, if not, keep the existing value
                String newBasic = (request.getBasic() != null && !request.getBasic().isEmpty()) ? request.getBasic() : existingNavSection.getBasic();
                // Check if the request contains a link value, if not, keep the existing value
                String newCentered = (request.getCentered() != null && !request.getCentered().isEmpty()) ? request.getCentered() : existingNavSection.getCentered();

                String newActive = (request.getActive() != null && !request.getActive().isEmpty()) ? request.getActive() : existingNavSection.getActive();

                // Update the existing NavSection with new or existing values
                existingNavSection.setBasic(newBasic);
                existingNavSection.setCentered(newCentered);
                existingNavSection.setActive(newActive);

                navSectionRepository.update(existingNavSection);

                // Convert NavSection to NavResponse
                NavResponse response = convertToNavResponse(existingNavSection);

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

    private NavSection createDefaultNavSection(UserEntity user) {
        NavSection defaultNavSection = new NavSection();
        defaultNavSection.setUser(user);
        defaultNavSection.setBasic("{\"type\":\"div\",\"props\":{\"id\":\"header-section\",\"style\":{\"display\":\"flex\",\"position\":\"fixed\",\"zIndex\":10,\"paddingTop\":\"0.5rem\",\"paddingBottom\":\"0.5rem\",\"paddingLeft\":\"3.5rem\",\"paddingRight\":\"3.5rem\",\"marginTop\":\"-1.25rem\",\"justifyContent\":\"space-between\",\"alignItems\":\"center\",\"width\":\"100%\",\"backgroundColor\":\"#F5F5F5\"},\"children\":[{\"type\":\"a\",\"props\":{\"href\":\"#\",\"style\":{\"display\":\"flex\",\"alignItems\":\"center\",\"cursor\":\"pointer\"},\"children\":[{\"type\":\"img\",\"props\":{\"id\":\"logo\",\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"logo\",\"style\":{\"padding\":\"0.5rem\",\"borderRadius\":\"0.25rem\",\"width\":\"4rem\",\"height\":\"4rem\"}}},{\"type\":\"h1\",\"props\":{\"id\":\"navHeader\",\"className\":\"header-text\",\"style\":{\"fontWeight\":700,\"textTransform\":\"uppercase\",\"color\":\"#000000\"},\"children\":\"hamro patro\"}}]}},{\"type\":\"nav\",\"props\":{\"id\":\"navItems\",\"style\":{\"display\":\"flex\",\"justifyContent\":\"space-between\",\"alignItems\":\"center\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":500,\"gap\":\"25px\",\"color\":\"#000000\"},\"children\":[{\"type\":\"a\",\"props\":{\"id\":\"navHome\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#\",\"children\":\"Home\"}},{\"type\":\"a\",\"props\":{\"id\":\"navAbout\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#about\",\"children\":\"About\"}},{\"type\":\"a\",\"props\":{\"id\":\"navServices\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#services\",\"children\":\"Services\"}},{\"type\":\"a\",\"props\":{\"id\":\"navContact\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#contact\",\"children\":\"Contact Us\"}}]}}]}}");
        defaultNavSection.setCentered("{\"type\":\"div\",\"props\":{\"id\":\"header-section\",\"style\":{\"background\":\"#F5F5F5\",\"display\":\"flex\",\"position\":\"fixed\",\"zIndex\":10,\"paddingTop\":\"0.5rem\",\"paddingBottom\":\"0.5rem\",\"paddingLeft\":\"3.5rem\",\"paddingRight\":\"3.5rem\",\"marginTop\":\"-1.25rem\",\"justifyContent\":\"space-between\",\"alignItems\":\"center\",\"width\":\"100%\",\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\",\":hover\":{\"boxShadow\":\"0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)\"}},\"children\":[{\"type\":\"a\",\"props\":{\"href\":\"#\",\"style\":{\"display\":\"flex\",\"gap\":\"1rem\",\"justifyContent\":\"center\",\"alignItems\":\"center\"},\"children\":{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"img\",\"props\":{\"id\":\"logo\",\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"logo\",\"style\":{\"height\":\"3.5rem\",\"width\":\"3.5rem\",\"borderRadius\":\"0.375rem\"}}}}}}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"alignItems\":\"center\"},\"children\":[{\"type\":\"a\",\"props\":{\"id\":\"navHeader\",\"className\":\"header-text\",\"href\":\"#\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":700,\"textTransform\":\"uppercase\",\"color\":\"#000000\"},\"children\":\"hamro patro\"}},{\"type\":\"nav\",\"props\":{\"id\":\"navItems\",\"style\":{\"display\":\"flex\",\"paddingTop\":\"0.25rem\",\"paddingBottom\":\"0.25rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":500,\"gap\":\"25px\",\"color\":\"#000000\"},\"children\":[{\"type\":\"a\",\"props\":{\"id\":\"navHome\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#\",\"children\":\"Home\"}},{\"type\":\"a\",\"props\":{\"id\":\"navAbout\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#about\",\"children\":\"About\"}},{\"type\":\"a\",\"props\":{\"id\":\"navServices\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#services\",\"children\":\"Services\"}},{\"type\":\"a\",\"props\":{\"id\":\"navContact\",\"className\":\"header-text\",\"style\":{\"color\":\"#000000\"},\"href\":\"#contact\",\"children\":\"Contact Us\"}}]}}]}},{\"type\":\"div\",\"props\":{\"children\":\"\"}}]}}");
        defaultNavSection.setActive("Basic");
        return defaultNavSection;
    }

    private NavResponse convertToNavResponse(NavSection navSection) {
        return NavResponse.newBuilder()
                .setBasic(navSection.getBasic())
                .setCentered(navSection.getCentered())
                .setActive(navSection.getActive())
                .build();
    }

}
