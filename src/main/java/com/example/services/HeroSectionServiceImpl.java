package com.example.services;

import com.example.HeroSectionResponse;
import com.example.HeroSectionServiceGrpc;
import com.example.HeroSectionUpdateRequest;
import com.example.entity.HeroSection;
import com.example.entity.UserEntity;
import com.example.repository.HeroSectionRepository;
import com.example.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@GrpcService
public class HeroSectionServiceImpl extends HeroSectionServiceGrpc.HeroSectionServiceImplBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private HeroSectionRepository heroSectionRepository;

    @Override
    @Transactional
    public void updateHeroSection(HeroSectionUpdateRequest request, StreamObserver<HeroSectionResponse> responseObserver) {
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

            // Find the HeroSection for the user
            HeroSection existingHeroSection = heroSectionRepository.findByUser(existingUser).orElse(null);

            if (existingHeroSection == null) {
                // User is logging in for the first time, create a new HeroSection record with default values.
                HeroSection defaultHeroSection = createDefaultHeroSection(existingUser);
                heroSectionRepository.save(defaultHeroSection);

                // Convert HeroSection to HeroSectionResponse
                HeroSectionResponse response = convertToHeroSectionResponse(defaultHeroSection);

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                // User is updating the HeroSection

                // Check if the request contains a split value, if not, keep the existing value
                String newSplit = (request.getSplit() != null && !request.getSplit().isEmpty()) ? request.getSplit() : existingHeroSection.getSplit();
                // Check if the request contains a centered value, if not, keep the existing value
                String newCentered = (request.getCentered() != null && !request.getCentered().isEmpty()) ? request.getCentered() : existingHeroSection.getCentered();
                // Check if the request contains an active value, if not, keep the existing value
                String newActive = (request.getActive() != null && !request.getActive().isEmpty()) ? request.getActive() : existingHeroSection.getActive();

                // Update the existing HeroSection with new or existing values
                existingHeroSection.setSplit(newSplit);
                existingHeroSection.setCentered(newCentered);
                existingHeroSection.setActive(newActive);

                heroSectionRepository.update(existingHeroSection);

                // Convert HeroSection to HeroSectionResponse
                HeroSectionResponse response = convertToHeroSectionResponse(existingHeroSection);

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


    private HeroSection createUpdatedHeroSection(HeroSection existingHeroSection, HeroSectionUpdateRequest request) {
        // Update the existing HeroSection with new values
        existingHeroSection.setSplit(request.getSplit());
        existingHeroSection.setCentered(request.getCentered());
        existingHeroSection.setActive(request.getActive());
        return existingHeroSection;
    }

    private HeroSectionResponse convertToHeroSectionResponse(HeroSection heroSection) {
        return HeroSectionResponse.newBuilder()
//                .setId(heroSection.getId())
                .setSplit(heroSection.getSplit())
                .setCentered(heroSection.getCentered())
                .setActive(heroSection.getActive())
                .build();
    }


    private HeroSection createDefaultHeroSection(UserEntity user) {
        HeroSection defaultHeroSection = new HeroSection();
        defaultHeroSection.setUser(user);
        // Set default values (you may use configuration or hardcode values here)
        defaultHeroSection.setSplit("{\"type\":\"div\",\"props\":{\"id\":\"hero-split\",\"style\":{\"backgroundColor\":\"#FFFFFF\",\"height\":\"85vh\",\"marginBottom\":\"1.25rem\",\"marginTop\":\"1.25rem\",\"paddingBottom\":\"5rem\",\"paddingTop\":\"5rem\"},\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"grid\",\"gap\":\"8rem\",\"gridTemplateColumns\":\"repeat(2, minmax(0, 1fr))\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"alignItems\":\"center\",\"display\":\"flex\",\"justifyContent\":\"center\",\"left\":\"1.25rem\",\"position\":\"relative\",\"top\":\"5rem\"},\"children\":{\"type\":\"img\",\"props\":{\"alt\":\"Banner\",\"height\":600,\"id\":\"hero-banner\",\"src\":\"https://images.unsplash.com/photo-1542744173-8e7e53415bb0?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D\",\"style\":{\"borderRadius\":\"0.25rem\",\"boxShadow\":\"0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)\",\"marginTop\":\"0.5rem\"},\"width\":550}}}},{\"type\":\"p\",\"props\":{\"id\":\"hero-text\",\"style\":{\"fontSize\":\"1.875rem\",\"lineHeight\":\"2.25rem\",\"position\":\"relative\",\"right\":\"6rem\",\"textAlign\":\"center\",\"top\":\"11rem\",\"userSelect\":\"none\",\"color\":\"#000000\"},\"children\":\"At Hamro Patro, we design seamless digital experiences . Explore our cutting-edge products for convenience that elevates your digital lifestyle .\"}},{\"type\":\"a\",\"props\":{\"href\":\"#services\",\"style\":{\":hover\":{\"color\":\"#E50914\"},\"backgroundColor\":\"#DC2626\",\"borderRadius\":\"0.375rem\",\"bottom\":\"6rem\",\"color\":\"#FFFFFF\",\"fontSize\":\"1.125rem\",\"fontWeight\":500,\"lineHeight\":\"1.75rem\",\"paddingBottom\":\"0.5rem\",\"paddingLeft\":\"1rem\",\"paddingRight\":\"1rem\",\"paddingTop\":\"0.5rem\",\"position\":\"absolute\",\"right\":\"9rem\"},\"children\":\"Explore our products\"}}]}}}}");
        defaultHeroSection.setCentered("{\"type\":\"div\",\"props\":{\"id\":\"hero-centered\",\"style\":{\"marginTop\":\"1.25rem\",\"height\":\"100vh\",\"width\":\"100%\",\"backgroundSize\":\"cover\",\"background\":\"url('https://images.unsplash.com/photo-1542744173-8e7e53415bb0?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D')\",\"backgroundRepeat\":\"no-repeat\",\"backgroundPosition\":\"center center\"},\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"position\":\"absolute\",\"right\":0,\"width\":\"100%\",\"height\":\"100%\"},\"children\":{\"type\":\"p\",\"props\":{\"id\":\"hero-text\",\"style\":{\"position\":\"absolute\",\"right\":\"5rem\",\"marginTop\":\"16rem\",\"marginBottom\":\"16rem\",\"width\":\"50%\",\"fontSize\":\"1.875rem\",\"lineHeight\":\"2.25rem\",\"fontWeight\":500,\"textAlign\":\"center\",\"userSelect\":\"none\",\"color\":\"#000000\"},\"children\":\"At Hamro Patro, we design seamless digital experiences . Explore our cutting-edge products for convenience that elevates your digital lifestyle .\"}}}}}}");
        defaultHeroSection.setActive("Split");
        return defaultHeroSection;
    }
}
