package com.example.services;

import com.example.DetailRequest;
import com.example.DetailResponse;
import com.example.DetailSectionServiceGrpc;
import com.example.entity.DetailSection;
import com.example.entity.UserEntity;
import com.example.repository.DetailSectionRepository;
import com.example.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@GrpcService
public class DetailSectionServiceImpl extends DetailSectionServiceGrpc.DetailSectionServiceImplBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private DetailSectionRepository detailSectionRepository;

    @Override
    @Transactional
    public void updateDetailSection(DetailRequest request, StreamObserver<DetailResponse> responseObserver) {
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

            // Find the DetailSection for the user
            DetailSection existingDetailSection = detailSectionRepository.findByUser(existingUser).orElse(null);

            if (existingDetailSection == null) {
                // User is logging in for the first time, create a new DetailSection record with default values.
                DetailSection defaultDetailSection = createDefaultDetailSection(existingUser);
                detailSectionRepository.save(defaultDetailSection);

                // Convert DetailSection to DetailResponse
                DetailResponse response = convertToDetailResponse(defaultDetailSection);

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                // User is updating the DetailSection

                // Check if the request contains a split value, if not, keep the existing value
                String newSplit = (request.getSplit() != null && !request.getSplit().isEmpty()) ? request.getSplit() : existingDetailSection.getSplit();
                // Check if the request contains a tile value, if not, keep the existing value
                String newTile = (request.getTile() != null && !request.getTile().isEmpty()) ? request.getTile() : existingDetailSection.getTile();
                String newActive = (request.getActive() != null && !request.getActive().isEmpty()) ? request.getActive() : existingDetailSection.getActive();

                // Update the existing DetailSection with new or existing values
                existingDetailSection.setSplit(newSplit);
                existingDetailSection.setTile(newTile);
                existingDetailSection.setActive(newActive);

                detailSectionRepository.update(existingDetailSection);

                // Convert DetailSection to DetailResponse
                DetailResponse response = convertToDetailResponse(existingDetailSection);

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

    private DetailSection createDefaultDetailSection(UserEntity user) {
        DetailSection defaultDetailSection = new DetailSection();
        defaultDetailSection.setUser(user);
        defaultDetailSection.setSplit("{\"type\":\"div\",\"props\":{\"id\":\"detail-section\",\"style\":{\"paddingTop\":\"6rem\",\"paddingBottom\":\"6rem\",\"height\":\"100vh\",\"backgroundColor\":\"#F5F5F5\"},\"children\":[{\"type\":\"h1\",\"props\":{\"id\":\"about\",\"style\":{\"fontSize\":\"3rem\",\"lineHeight\":1,\"fontWeight\":600,\"textAlign\":\"center\",\"color\":\"#DC2626\",\"textTransform\":\"capitalize\"},\"children\":\"About us\"}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingLeft\":\"4rem\",\"paddingRight\":\"4rem\",\"paddingTop\":\"1.5rem\",\"gap\":\"2.5rem\",\"justifyContent\":\"space-between\",\"alignItems\":\"center\"},\"children\":[{\"type\":\"div\",\"props\":{\"id\":\"section-1\",\"style\":{\"paddingLeft\":\"2.5rem\",\"paddingRight\":\"2.5rem\",\"paddingTop\":\"3.5rem\",\"paddingBottom\":\"3.5rem\",\"borderRadius\":\"0.375rem\",\"borderWidth\":\"2px\",\"width\":\"50%\",\"backgroundColor\":\"#DC2626\"},\"children\":[{\"type\":\"h1\",\"props\":{\"id\":\"welcomeHeading\",\"style\":{\"fontSize\":\"3rem\",\"lineHeight\":1,\"fontWeight\":600,\"color\":\"#FFFFFF\",\"textTransform\":\"capitalize\"},\"children\":\"Welcome\"}},{\"type\":\"p\",\"props\":{\"id\":\"welcomeText\",\"style\":{\"paddingTop\":\"0.5rem\",\"fontSize\":\"1.25rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":400,\"textAlign\":\"left\",\"color\":\"#FFFFFF\"},\"children\":\"to Hamro Patro, a leading IT company dedicated to revolutionizing the digital landscape. With a relentless commitment to innovation, we bring you a diverse portfolio of cutting-edge products designed to simplify and enhance your online experience.\"}}]}},{\"type\":\"div\",\"props\":{\"id\":\"section-2\",\"style\":{\"display\":\"flex\",\"paddingLeft\":\"4rem\",\"paddingRight\":\"4rem\",\"paddingTop\":\"8rem\",\"paddingBottom\":\"8rem\",\"marginTop\":\"-2.5rem\",\"flexDirection\":\"column\",\"alignItems\":\"center\",\"borderRadius\":\"9999px\",\"borderColor\":[\"#DC2626\",\"2px\"],\"backgroundColor\":\"#FFFFFF\",\"width\":\"39%\"},\"children\":[{\"type\":\"h1\",\"props\":{\"id\":\"visionHeading\",\"style\":{\"position\":\"relative\",\"top\":\"-2rem\",\"fontSize\":\"3rem\",\"lineHeight\":1,\"fontWeight\":600,\"color\":\"#DC2626\",\"textTransform\":\"capitalize\"},\"children\":\"our vision\"}},{\"type\":\"p\",\"props\":{\"id\":\"visionText\",\"style\":{\"padding\":\"1rem\",\"paddingTop\":\"0.75rem\",\"paddingBottom\":\"0.75rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":500,\"textAlign\":\"center\",\"userSelect\":\"none\"},\"children\":\"Empowering individuals and businesses through the limitless possibilities of technology. We envision a world where connectivity, convenience, and creativity converge to shape a brighter and more accessible future.\"}}]}}]}}]}}");
        defaultDetailSection.setTile("{\"type\":\"div\",\"props\":{\"id\":\"detail-section\",\"style\":{\"display\":\"flex\",\"paddingTop\":\"6rem\",\"paddingBottom\":\"6rem\",\"gap\":\"2.5rem\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"paddingLeft\":\"60px\",\"paddingRight\":\"16px\",\"paddingTop\":\"110px\",\"paddingBottom\":\"10px\",\"marginTop\":\"32px\",\"height\":\"16rem\",\"backgroundColor\":\"#DC2626\"},\"children\":[{\"type\":\"h1\",\"props\":{\"id\":\"welcomeHeading\",\"style\":{\"marginTop\":\"-6rem\",\"fontSize\":\"3rem\",\"lineHeight\":1,\"fontWeight\":600,\"color\":\"#FFFFFF\",\"textTransform\":\"capitalize\"},\"children\":\"About us\"}},{\"type\":\"p\",\"props\":{\"id\":\"welcomeText\",\"style\":{\"paddingTop\":\"1.25rem\",\"fontSize\":\"1.25rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":400,\"color\":\"#FFFFFF\"},\"children\":\"Welcome to Hamro Patro, a leading IT company dedicated to revolutionizing the digital landscape. With a relentless commitment to innovation, we bring you a diverse portfolio of cutting-edge products designed to simplify and enhance your online experience.\"}},{\"type\":\"div\",\"props\":{\"style\":{\"position\":\"relative\",\"top\":\"7rem\"},\"children\":[{\"type\":\"h1\",\"props\":{\"id\":\"visionHeading\",\"style\":{\"fontSize\":\"3rem\",\"lineHeight\":1,\"fontWeight\":500,\"color\":\"#DC2626\"},\"children\":\"Our Vision:\"}},{\"type\":\"p\",\"props\":{\"id\":\"visionText\",\"style\":{\"paddingTop\":\"0.75rem\",\"paddingBottom\":\"0.75rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":500},\"children\":\"Empowering individuals and businesses through the limitless possibilities of technology. We envision a world where connectivity, convenience, and creativity converge to shape a brighter and more accessible future.\"}}]}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingRight\":\"4rem\",\"paddingTop\":\"31px\",\"gap\":\"2.5rem\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"gap\":\"25px\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"justifyContent\":\"center\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"width\":\"12rem\",\"height\":\"12rem\",\"backgroundColor\":\"#FFFFFF\",\"cursor\":\"pointer\",\"boxShadow\":\"0 1px 2px 0 rgba(0, 0, 0, 0.05)\",\":hover\":{\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\"}},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"PiUsersBold\",\"props\":{\"size\":60}}}},{\"type\":\"h1\",\"props\":{\"id\":\"teamCount\",\"style\":{\"paddingTop\":\"1.25rem\",\"fontSize\":\"2.25rem\",\"lineHeight\":\"2.5rem\",\"fontWeight\":500},\"children\":\"80+\"}},{\"type\":\"h2\",\"props\":{\"style\":{\"paddingTop\":\"0.75rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"textTransform\":\"uppercase\"},\"children\":\"TEAM MEMBERS\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"justifyContent\":\"center\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"width\":\"12rem\",\"height\":\"12rem\",\"backgroundColor\":\"#FFFFFF\",\"cursor\":\"pointer\",\"boxShadow\":\"0 1px 2px 0 rgba(0, 0, 0, 0.05)\",\":hover\":{\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\"}},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"PiStackBold\",\"props\":{\"size\":60}}}},{\"type\":\"h1\",\"props\":{\"id\":\"projectsCount\",\"style\":{\"paddingTop\":\"1.25rem\",\"fontSize\":\"2.25rem\",\"lineHeight\":\"2.5rem\",\"fontWeight\":500},\"children\":\"10+\"}},{\"type\":\"h2\",\"props\":{\"style\":{\"paddingTop\":\"0.75rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"textTransform\":\"uppercase\"},\"children\":\"projects\"}}]}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"gap\":\"1.5rem\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"justifyContent\":\"center\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"width\":\"12rem\",\"height\":\"12rem\",\"backgroundColor\":\"#FFFFFF\",\"cursor\":\"pointer\",\"boxShadow\":\"0 1px 2px 0 rgba(0, 0, 0, 0.05)\",\":hover\":{\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\"}},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"TbMoodHappy\",\"props\":{\"size\":60}}}},{\"type\":\"h1\",\"props\":{\"id\":\"usersCount\",\"style\":{\"paddingTop\":\"1.25rem\",\"fontSize\":\"2.25rem\",\"lineHeight\":\"2.5rem\",\"fontWeight\":500},\"children\":\"10M+\"}},{\"type\":\"h2\",\"props\":{\"style\":{\"paddingTop\":\"0.75rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"textTransform\":\"uppercase\"},\"children\":\"happy users\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"justifyContent\":\"center\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"width\":\"12rem\",\"height\":\"12rem\",\"backgroundColor\":\"#FFFFFF\",\"cursor\":\"pointer\",\"boxShadow\":\"0 1px 2px 0 rgba(0, 0, 0, 0.05)\",\":hover\":{\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\"}},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"IoLocation\",\"props\":{\"size\":60}}}},{\"type\":\"h1\",\"props\":{\"id\":\"officesCount\",\"style\":{\"paddingTop\":\"1.25rem\",\"fontSize\":\"2.25rem\",\"lineHeight\":\"2.5rem\",\"fontWeight\":500},\"children\":\"1\"}},{\"type\":\"h2\",\"props\":{\"style\":{\"paddingTop\":\"0.75rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"textTransform\":\"uppercase\"},\"children\":\"office\"}}]}}]}}]}}]}}");
        defaultDetailSection.setActive("Tile");
        return defaultDetailSection;
    }

    private DetailResponse convertToDetailResponse(DetailSection detailSection) {
        return DetailResponse.newBuilder()
                .setSplit(detailSection.getSplit())
                .setTile(detailSection.getTile())
                .setActive(detailSection.getActive())
                .build();
    }
}
