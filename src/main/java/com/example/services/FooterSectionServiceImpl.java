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
        defaultFooterSection.setBasic("footer");
        defaultFooterSection.setCentered("Default Centered");
        defaultFooterSection.setActive("Default Active");
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
