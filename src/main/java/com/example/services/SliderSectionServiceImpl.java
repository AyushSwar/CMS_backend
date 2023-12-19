package com.example.services;

import com.example.SliderRequest;
import com.example.SliderResponse;
import com.example.SliderSectionServiceGrpc;
import com.example.entity.SliderSection;
import com.example.entity.UserEntity;
import com.example.repository.SliderSectionRepository;
import com.example.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@GrpcService
public class SliderSectionServiceImpl extends SliderSectionServiceGrpc.SliderSectionServiceImplBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private SliderSectionRepository sliderSectionRepository;

    @Override
    @Transactional
    public void updateSliderSection(SliderRequest request, StreamObserver<SliderResponse> responseObserver) {
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

            // Find the SliderSection for the user
            SliderSection existingSliderSection = sliderSectionRepository.findByUser(existingUser).orElse(null);

            if (existingSliderSection == null) {
                // User is logging in for the first time, create a new SliderSection record with default values.
                SliderSection defaultSliderSection = createDefaultSliderSection(existingUser);
                sliderSectionRepository.save(defaultSliderSection);

                // Convert SliderSection to SliderResponse
                SliderResponse response = convertToSliderResponse(defaultSliderSection);

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                // User is updating the SliderSection

                // Check if the request contains a basic value, if not, keep the existing value
                String newBasic = (request.getBasic() != null && !request.getBasic().isEmpty()) ? request.getBasic() : existingSliderSection.getBasic();

                // Update the existing SliderSection with new or existing values
                existingSliderSection.setBasic(newBasic);

                sliderSectionRepository.update(existingSliderSection);

                // Convert SliderSection to SliderResponse
                SliderResponse response = convertToSliderResponse(existingSliderSection);

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

    private SliderSection createDefaultSliderSection(UserEntity user) {
        SliderSection defaultSliderSection = new SliderSection();
        defaultSliderSection.setUser(user);
        defaultSliderSection.setBasic("{\"type\":\"div\",\"props\":{\"style\":{\"paddingTop\":\"6rem\",\"height\":\"100vh\"},\"id\":\"services\",\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"fontSize\":\"3rem\",\"lineHeight\":\"1\",\"fontWeight\":\"600\",\"textAlign\":\"center\",\"textTransform\":\"capitalize\"},\"children\":\"Services we Offer\"}},{\"type\":\"p\",\"props\":{\"style\":{\"paddingLeft\":\"7rem\",\"paddingRight\":\"7rem\",\"paddingTop\":\"2.5rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"textAlign\":\"center\"},\"children\":\"Lorem ipsum dolor sit amet consectetur, adipisicing elit. Debitis tenetur libero quas unde, odio cupiditate nisi deserunt odit expedita non tempora commodi aperiam maiores aut sed ipsa accusantium voluptate alias amet sint nostrum dolore et corporis. Fuga consectetur sed\"}},{\"type\":\"div\",\"props\":{\"style\":{\"overflow\":\"hidden\",\"width\":\"50%\",\"margin\":\"auto\"},\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"marginTop\":\"8rem\",\"marginBottom\":\"8rem\"},\"children\":{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"Slider\",\"props\":{\"dots\":true,\"infinite\":true,\"slidesToShow\":3,\"slidesToScroll\":1,\"autoplay\":true,\"speed\":2000,\"autoplaySpeed\":2000,\"cssEase\":\"linear\",\"children\":[{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"gap\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"gap\":\"0.5rem\",\"alignItems\":\"flex-start\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"\",\"style\":{\"height\":\"2.5rem\"}}},{\"type\":\"h4\",\"props\":{\"style\":{\"fontWeight\":500,\"textTransform\":\"capitalize\"},\"children\":\"Hamro Health\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702374511/l4loc0hkk5dwkdehyncp.jpg\",\"alt\":\"slider_image\",\"style\":{\"objectFit\":\"cover\",\"width\":\"100%\",\"height\":\"9rem\"}}},{\"type\":\"p\",\"props\":{\"style\":{\"textAlign\":\"center\"},\"children\":\"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"}}]}}]}}}},{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"gap\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"gap\":\"0.5rem\",\"alignItems\":\"flex-start\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"\",\"style\":{\"height\":\"2.5rem\"}}},{\"type\":\"h4\",\"props\":{\"style\":{\"fontWeight\":500,\"textTransform\":\"capitalize\"},\"children\":\"Hamro Remit\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374612/hh0pnll84a2busyek7qv.jpg\",\"alt\":\"slider_image\",\"style\":{\"objectFit\":\"cover\",\"width\":\"100%\",\"height\":\"9rem\"}}},{\"type\":\"p\",\"props\":{\"style\":{\"textAlign\":\"center\"},\"children\":\"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"}}]}}]}}}},{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"gap\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"gap\":\"0.5rem\",\"alignItems\":\"flex-start\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"\",\"style\":{\"height\":\"2.5rem\"}}},{\"type\":\"h4\",\"props\":{\"style\":{\"fontWeight\":500,\"textTransform\":\"capitalize\"},\"children\":\"Hamro Recharge\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374642/jozuh877cfq2arntlnp6.jpg\",\"alt\":\"slider_image\",\"style\":{\"objectFit\":\"cover\",\"width\":\"100%\",\"height\":\"9rem\"}}},{\"type\":\"p\",\"props\":{\"style\":{\"textAlign\":\"center\"},\"children\":\"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"}}]}}]}}}},{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"gap\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"gap\":\"0.5rem\",\"alignItems\":\"flex-start\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"\",\"style\":{\"height\":\"2.5rem\"}}},{\"type\":\"h4\",\"props\":{\"style\":{\"fontWeight\":500,\"textTransform\":\"capitalize\"},\"children\":\"Hamro Gifts\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374681/gtopxnmx0uyspdg9ivpj.jpg \",\"alt\":\"slider_image\",\"style\":{\"objectFit\":\"cover\",\"width\":\"100%\",\"height\":\"9rem\"}}},{\"type\":\"p\",\"props\":{\"style\":{\"textAlign\":\"center\"},\"children\":\"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"}}]}}]}}}},{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"gap\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"gap\":\"0.5rem\",\"alignItems\":\"flex-start\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"\",\"style\":{\"height\":\"2.5rem\"}}},{\"type\":\"h4\",\"props\":{\"style\":{\"fontWeight\":500,\"textTransform\":\"capitalize\"},\"children\":\"Hamro Jyotish\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374704/o9smwvgrajtbtsfowenb.jpg\",\"alt\":\"slider_image\",\"style\":{\"objectFit\":\"cover\",\"width\":\"100%\",\"height\":\"9rem\"}}},{\"type\":\"p\",\"props\":{\"style\":{\"textAlign\":\"center\"},\"children\":\"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"}}]}}]}}}},{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1rem\",\"flexDirection\":\"column\",\"gap\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"gap\":\"0.5rem\",\"alignItems\":\"flex-start\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\"alt\":\"\",\"style\":{\"height\":\"2.5rem\"}}},{\"type\":\"h4\",\"props\":{\"style\":{\"fontWeight\":500,\"textTransform\":\"capitalize\"},\"children\":\"Hamro Pay\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\"},\"children\":[{\"type\":\"img\",\"props\":{\"src\":\"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374731/gpbzg6cn7q0yexfmeumj.jpg\",\"alt\":\"slider_image\",\"style\":{\"objectFit\":\"cover\",\"width\":\"100%\",\"height\":\"9rem\"}}},{\"type\":\"p\",\"props\":{\"style\":{\"textAlign\":\"center\"},\"children\":\"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"}}]}}]}}}}]}}}}}}}}]}}");
        return defaultSliderSection;
    }

    private SliderResponse convertToSliderResponse(SliderSection sliderSection) {
        return SliderResponse.newBuilder()
                .setBasic(sliderSection.getBasic())
                .build();
    }
}
