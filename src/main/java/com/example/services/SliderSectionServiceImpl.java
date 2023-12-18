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
        defaultSliderSection.setBasic(
                "{\n" +
                "    \"type\": \"div\",\n" +
                "    \"props\": {\n" +
                "        \"style\": {\n" +
                "            \"paddingTop\": \"6rem\",\n" +
                "            \"height\": \"100vh\"\n" +
                "        },\n" +
                "        \"id\": \"services\",\n" +
                "        \"children\": [\n" +
                "            {\n" +
                "                \"type\": \"h1\",\n" +
                "                \"props\": {\n" +
                "                    \"style\": {\n" +
                "                        \"fontSize\": \"3rem\",\n" +
                "                        \"lineHeight\": \"1\",\n" +
                "                        \"fontWeight\": \"600\",\n" +
                "                        \"textAlign\": \"center\",\n" +
                "                        \"textTransform\": \"capitalize\"\n" +
                "                    },\n" +
                "                    \"children\": \"Services we Offer\"\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"type\": \"p\",\n" +
                "                \"props\": {\n" +
                "                    \"style\": {\n" +
                "                        \"paddingLeft\": \"7rem\",\n" +
                "                        \"paddingRight\": \"7rem\",\n" +
                "                        \"paddingTop\": \"2.5rem\",\n" +
                "                        \"fontSize\": \"1.125rem\",\n" +
                "                        \"lineHeight\": \"1.75rem\",\n" +
                "                        \"textAlign\": \"center\"\n" +
                "                    },\n" +
                "                    \"children\": \"Lorem ipsum dolor sit amet consectetur, adipisicing elit. Debitis tenetur libero quas unde, odio cupiditate nisi deserunt odit expedita non tempora commodi aperiam maiores aut sed ipsa accusantium voluptate alias amet sint nostrum dolore et corporis. Fuga consectetur sed\"\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"type\": \"div\",\n" +
                "                \"props\": {\n" +
                "                    \"style\": {\n" +
                "                        \"overflow\": \"hidden\",\n" +
                "                        \"width\": \"50%\",\n" +
                "                        \"margin\": \"auto\"\n" +
                "                    },\n" +
                "                    \"children\": {\n" +
                "                        \"type\": \"div\",\n" +
                "                        \"props\": {\n" +
                "                            \"style\": {\n" +
                "                                \"marginTop\": \"8rem\",\n" +
                "                                \"marginBottom\": \"8rem\"\n" +
                "                            },\n" +
                "                            \"children\": {\n" +
                "                                \"type\": \"div\",\n" +
                "                                \"props\": {\n" +
                "                                    \"children\": {\n" +
                "                                        \"type\": \"Slider\",\n" +
                "                                        \"props\": {\n" +
                "                                            \"dots\": true,\n" +
                "                                            \"infinite\": true,\n" +
                "                                            \"slidesToShow\": 3,\n" +
                "                                            \"slidesToScroll\": 1,\n" +
                "                                            \"autoplay\": true,\n" +
                "                                            \"speed\": 2000,\n" +
                "                                            \"autoplaySpeed\": 2000,\n" +
                "                                            \"cssEase\": \"linear\",\n" +
                "                                            \"children\": [\n" +
                "                                                {\n" +
                "                                                    \"type\": \"div\",\n" +
                "                                                    \"props\": {\n" +
                "                                                        \"children\": {\n" +
                "                                                            \"type\": \"div\",\n" +
                "                                                            \"props\": {\n" +
                "                                                                \"style\": {\n" +
                "                                                                    \"display\": \"flex\",\n" +
                "                                                                    \"padding\": \"1rem\",\n" +
                "                                                                    \"flexDirection\": \"column\",\n" +
                "                                                                    \"gap\": \"0.75rem\",\n" +
                "                                                                    \"borderRadius\": \"0.375rem\",\n" +
                "                                                                    \"backgroundColor\": \"#ffffff\"\n" +
                "                                                                },\n" +
                "                                                                \"children\": [\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"gap\": \"0.5rem\",\n" +
                "                                                                                \"alignItems\": \"flex-start\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\n" +
                "                                                                                        \"alt\": \"\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"height\": \"2.5rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"h4\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"fontWeight\": 500,\n" +
                "                                                                                            \"textTransform\": \"capitalize\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Hamro Health\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    },\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"flexDirection\": \"column\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702374511/l4loc0hkk5dwkdehyncp.jpg\",\n" +
                "                                                                                        \"alt\": \"slider_image\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"objectFit\": \"cover\",\n" +
                "                                                                                            \"width\": \"100%\",\n" +
                "                                                                                            \"height\": \"9rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"p\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"textAlign\": \"center\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    }\n" +
                "                                                                ]\n" +
                "                                                            }\n" +
                "                                                        }\n" +
                "                                                    }\n" +
                "                                                },\n" +
                "                                                {\n" +
                "                                                    \"type\": \"div\",\n" +
                "                                                    \"props\": {\n" +
                "                                                        \"children\": {\n" +
                "                                                            \"type\": \"div\",\n" +
                "                                                            \"props\": {\n" +
                "                                                                \"style\": {\n" +
                "                                                                    \"display\": \"flex\",\n" +
                "                                                                    \"padding\": \"1rem\",\n" +
                "                                                                    \"flexDirection\": \"column\",\n" +
                "                                                                    \"gap\": \"0.75rem\",\n" +
                "                                                                    \"borderRadius\": \"0.375rem\",\n" +
                "                                                                    \"backgroundColor\": \"#ffffff\"\n" +
                "                                                                },\n" +
                "                                                                \"children\": [\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"gap\": \"0.5rem\",\n" +
                "                                                                                \"alignItems\": \"flex-start\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\n" +
                "                                                                                        \"alt\": \"\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"height\": \"2.5rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"h4\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"fontWeight\": 500,\n" +
                "                                                                                            \"textTransform\": \"capitalize\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Hamro Remit\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    },\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"flexDirection\": \"column\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374612/hh0pnll84a2busyek7qv.jpg\",\n" +
                "                                                                                        \"alt\": \"slider_image\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"objectFit\": \"cover\",\n" +
                "                                                                                            \"width\": \"100%\",\n" +
                "                                                                                            \"height\": \"9rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"p\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"textAlign\": \"center\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    }\n" +
                "                                                                ]\n" +
                "                                                            }\n" +
                "                                                        }\n" +
                "                                                    }\n" +
                "                                                },\n" +
                "                                                {\n" +
                "                                                    \"type\": \"div\",\n" +
                "                                                    \"props\": {\n" +
                "                                                        \"children\": {\n" +
                "                                                            \"type\": \"div\",\n" +
                "                                                            \"props\": {\n" +
                "                                                                \"style\": {\n" +
                "                                                                    \"display\": \"flex\",\n" +
                "                                                                    \"padding\": \"1rem\",\n" +
                "                                                                    \"flexDirection\": \"column\",\n" +
                "                                                                    \"gap\": \"0.75rem\",\n" +
                "                                                                    \"borderRadius\": \"0.375rem\",\n" +
                "                                                                    \"backgroundColor\": \"#ffffff\"\n" +
                "                                                                },\n" +
                "                                                                \"children\": [\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"gap\": \"0.5rem\",\n" +
                "                                                                                \"alignItems\": \"flex-start\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\n" +
                "                                                                                        \"alt\": \"\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"height\": \"2.5rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"h4\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"fontWeight\": 500,\n" +
                "                                                                                            \"textTransform\": \"capitalize\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Hamro Recharge\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    },\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"flexDirection\": \"column\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374642/jozuh877cfq2arntlnp6.jpg\",\n" +
                "                                                                                        \"alt\": \"slider_image\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"objectFit\": \"cover\",\n" +
                "                                                                                            \"width\": \"100%\",\n" +
                "                                                                                            \"height\": \"9rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"p\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"textAlign\": \"center\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    }\n" +
                "                                                                ]\n" +
                "                                                            }\n" +
                "                                                        }\n" +
                "                                                    }\n" +
                "                                                },\n" +
                "                                                {\n" +
                "                                                    \"type\": \"div\",\n" +
                "                                                    \"props\": {\n" +
                "                                                        \"children\": {\n" +
                "                                                            \"type\": \"div\",\n" +
                "                                                            \"props\": {\n" +
                "                                                                \"style\": {\n" +
                "                                                                    \"display\": \"flex\",\n" +
                "                                                                    \"padding\": \"1rem\",\n" +
                "                                                                    \"flexDirection\": \"column\",\n" +
                "                                                                    \"gap\": \"0.75rem\",\n" +
                "                                                                    \"borderRadius\": \"0.375rem\",\n" +
                "                                                                    \"backgroundColor\": \"#ffffff\"\n" +
                "                                                                },\n" +
                "                                                                \"children\": [\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"gap\": \"0.5rem\",\n" +
                "                                                                                \"alignItems\": \"flex-start\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\n" +
                "                                                                                        \"alt\": \"\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"height\": \"2.5rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"h4\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"fontWeight\": 500,\n" +
                "                                                                                            \"textTransform\": \"capitalize\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Hamro Gifts\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    },\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"flexDirection\": \"column\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374681/gtopxnmx0uyspdg9ivpj.jpg \",\n" +
                "                                                                                        \"alt\": \"slider_image\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"objectFit\": \"cover\",\n" +
                "                                                                                            \"width\": \"100%\",\n" +
                "                                                                                            \"height\": \"9rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"p\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"textAlign\": \"center\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    }\n" +
                "                                                                ]\n" +
                "                                                            }\n" +
                "                                                        }\n" +
                "                                                    }\n" +
                "                                                },\n" +
                "                                                {\n" +
                "                                                    \"type\": \"div\",\n" +
                "                                                    \"props\": {\n" +
                "                                                        \"children\": {\n" +
                "                                                            \"type\": \"div\",\n" +
                "                                                            \"props\": {\n" +
                "                                                                \"style\": {\n" +
                "                                                                    \"display\": \"flex\",\n" +
                "                                                                    \"padding\": \"1rem\",\n" +
                "                                                                    \"flexDirection\": \"column\",\n" +
                "                                                                    \"gap\": \"0.75rem\",\n" +
                "                                                                    \"borderRadius\": \"0.375rem\",\n" +
                "                                                                    \"backgroundColor\": \"#ffffff\"\n" +
                "                                                                },\n" +
                "                                                                \"children\": [\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"gap\": \"0.5rem\",\n" +
                "                                                                                \"alignItems\": \"flex-start\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\n" +
                "                                                                                        \"alt\": \"\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"height\": \"2.5rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"h4\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"fontWeight\": 500,\n" +
                "                                                                                            \"textTransform\": \"capitalize\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Hamro Jyotish\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    },\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"flexDirection\": \"column\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374704/o9smwvgrajtbtsfowenb.jpg\",\n" +
                "                                                                                        \"alt\": \"slider_image\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"objectFit\": \"cover\",\n" +
                "                                                                                            \"width\": \"100%\",\n" +
                "                                                                                            \"height\": \"9rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"p\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"textAlign\": \"center\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    }\n" +
                "                                                                ]\n" +
                "                                                            }\n" +
                "                                                        }\n" +
                "                                                    }\n" +
                "                                                },\n" +
                "                                                {\n" +
                "                                                    \"type\": \"div\",\n" +
                "                                                    \"props\": {\n" +
                "                                                        \"children\": {\n" +
                "                                                            \"type\": \"div\",\n" +
                "                                                            \"props\": {\n" +
                "                                                                \"style\": {\n" +
                "                                                                    \"display\": \"flex\",\n" +
                "                                                                    \"padding\": \"1rem\",\n" +
                "                                                                    \"flexDirection\": \"column\",\n" +
                "                                                                    \"gap\": \"0.75rem\",\n" +
                "                                                                    \"borderRadius\": \"0.375rem\",\n" +
                "                                                                    \"backgroundColor\": \"#ffffff\"\n" +
                "                                                                },\n" +
                "                                                                \"children\": [\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"gap\": \"0.5rem\",\n" +
                "                                                                                \"alignItems\": \"flex-start\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"https://res.cloudinary.com/dssvqu4bj/image/upload/v1702360102/grfykaebabrla6izorzr.webp\",\n" +
                "                                                                                        \"alt\": \"\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"height\": \"2.5rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"h4\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"fontWeight\": 500,\n" +
                "                                                                                            \"textTransform\": \"capitalize\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Hamro Pay\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    },\n" +
                "                                                                    {\n" +
                "                                                                        \"type\": \"div\",\n" +
                "                                                                        \"props\": {\n" +
                "                                                                            \"style\": {\n" +
                "                                                                                \"display\": \"flex\",\n" +
                "                                                                                \"flexDirection\": \"column\"\n" +
                "                                                                            },\n" +
                "                                                                            \"children\": [\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"img\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"src\": \"http://res.cloudinary.com/dssvqu4bj/image/upload/v1702374731/gpbzg6cn7q0yexfmeumj.jpg\",\n" +
                "                                                                                        \"alt\": \"slider_image\",\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"objectFit\": \"cover\",\n" +
                "                                                                                            \"width\": \"100%\",\n" +
                "                                                                                            \"height\": \"9rem\"\n" +
                "                                                                                        }\n" +
                "                                                                                    }\n" +
                "                                                                                },\n" +
                "                                                                                {\n" +
                "                                                                                    \"type\": \"p\",\n" +
                "                                                                                    \"props\": {\n" +
                "                                                                                        \"style\": {\n" +
                "                                                                                            \"textAlign\": \"center\"\n" +
                "                                                                                        },\n" +
                "                                                                                        \"children\": \"Lorem ipsum dolor sit amet consectetur adipisicing elit. Eius, magnam\"\n" +
                "                                                                                    }\n" +
                "                                                                                }\n" +
                "                                                                            ]\n" +
                "                                                                        }\n" +
                "                                                                    }\n" +
                "                                                                ]\n" +
                "                                                            }\n" +
                "                                                        }\n" +
                "                                                    }\n" +
                "                                                }\n" +
                "                                            ]\n" +
                "                                        }\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}");
        return defaultSliderSection;
    }

    private SliderResponse convertToSliderResponse(SliderSection sliderSection) {
        return SliderResponse.newBuilder()
                .setBasic(sliderSection.getBasic())
                .build();
    }
}
