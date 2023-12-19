package com.example.services;

import com.example.ContactRequest;
        import com.example.ContactResponse;
        import com.example.ContactSectionServiceGrpc;
        import com.example.entity.ContactSection;
        import com.example.entity.UserEntity;
        import com.example.repository.ContactSectionRepository;
        import com.example.repository.UserRepository;
        import io.grpc.Status;
        import io.grpc.stub.StreamObserver;
        import io.micronaut.grpc.annotation.GrpcService;
        import io.micronaut.transaction.annotation.Transactional;
        import jakarta.inject.Inject;
        import jakarta.inject.Singleton;

@Singleton
@GrpcService
public class ContactSectionServiceImpl extends ContactSectionServiceGrpc.ContactSectionServiceImplBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private ContactSectionRepository contactSectionRepository;

    @Override
    @Transactional
    public void updateContactSection(ContactRequest request, StreamObserver<ContactResponse> responseObserver) {
        try {
            // Find the user by ID
            UserEntity existingUser = userRepository.findById(request.getId()).orElse(null);


            if (existingUser == null) {
                // Handle the case when the user is not found
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("User not found in Contact Database.")
                        .asRuntimeException());
                return;
            }

            // Find the ContactSection for the user
            ContactSection existingContactSection = contactSectionRepository.findByUser(existingUser).orElse(null);

            if (existingContactSection == null) {
                // User is logging in for the first time, create a new ContactSection record with default values.
                ContactSection defaultContactSection = createDefaultContactSection(existingUser);
                contactSectionRepository.save(defaultContactSection);

                // Convert ContactSection to ContactResponse
                ContactResponse response = convertToContactResponse(defaultContactSection);

                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                // User is updating the ContactSection

                // Check if the request contains a title value, if not, keep the existing value
                String newTitle = (request.getTile() != null && !request.getTile().isEmpty()) ? request.getTile() : existingContactSection.getTile();
                // Check if the request contains a link value, if not, keep the existing value
                String newCentered = (request.getCentered() != null && !request.getCentered().isEmpty()) ? request.getCentered() : existingContactSection.getCentered();
                String newActive = (request.getActive() != null && !request.getActive().isEmpty()) ? request.getActive() : existingContactSection.getActive();

                // Update the existing ContactSection with new or existing values
                existingContactSection.setTile(newTitle);
                existingContactSection.setCentered(newCentered);
                existingContactSection.setActive(newActive);

                contactSectionRepository.update(existingContactSection);

                // Convert ContactSection to ContactResponse
                ContactResponse response = convertToContactResponse(existingContactSection);

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

    private ContactSection createDefaultContactSection(UserEntity user) {
        ContactSection defaultContactSection = new ContactSection();
        defaultContactSection.setUser(user);
        defaultContactSection.setTile("{\"type\":\"div\",\"props\":{\"id\":\"contact-section\",\"style\":{\"paddingTop\":\"6rem\",\"height\":\"100vh\"},\"children\":[{\"type\":\"div\",\"props\":{\"children\":{\"type\":\"h1\",\"props\":{\"id\":\"welcomeheading\",\"style\":{\"fontSize\":\"3rem\",\"lineHeight\":\"1\",\"fontWeight\":\"600\",\"textAlign\":\"center\",\"textTransform\":\"capitalize\",\"color\":\"#E50914\"},\"children\":\"Contact us\"}}}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingLeft\":\"6rem\",\"paddingRight\":\"6rem\",\"paddingTop\":\"8rem\",\"justifyContent\":\"space-evenly\",\"alignItems\":\"center\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1.5rem\",\"flexDirection\":\"column\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"width\":\"18rem\",\"height\":\"13rem\",\"backgroundColor\":\"#FFFFFF\",\"cursor\":\"pointer\",\"userSelect\":\"none\",\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\"},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"FaLocationDot\",\"props\":{\"size\":50}}}},{\"type\":\"h1\",\"props\":{\"id\":\"officeheading\",\"style\":{\"paddingTop\":\"1.25rem\",\"paddingBottom\":\"0.5rem\",\"fontSize\":\"1.5rem\",\"lineHeight\":\"2rem\",\"fontWeight\":\"700\",\"textTransform\":\"capitalize\"},\"children\":\"office\"}},{\"type\":\"h2\",\"props\":{\"id\":\"address1\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":\"500\",\"textTransform\":\"capitalize\"},\"children\":\"Jaya Bageshwori Road\"}},{\"type\":\"h2\",\"props\":{\"id\":\"address2\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":\"500\",\"textTransform\":\"capitalize\"},\"children\":\"Sifal-8, kathmandu 44600\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1.25rem\",\"flexDirection\":\"column\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"width\":\"18rem\",\"height\":\"13rem\",\"backgroundColor\":\"#FFFFFF\",\"cursor\":\"pointer\",\"userSelect\":\"none\",\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\"},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"FaPhoneAlt\",\"props\":{\"size\":50}}}},{\"type\":\"h1\",\"props\":{\"id\":\"phoneheading\",\"style\":{\"paddingTop\":\"1.25rem\",\"paddingBottom\":\"0.5rem\",\"fontSize\":\"1.5rem\",\"lineHeight\":\"2rem\",\"fontWeight\":\"700\",\"textTransform\":\"capitalize\"},\"children\":\"phone number\"}},{\"type\":\"h2\",\"props\":{\"id\":\"phone1\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":\"500\",\"textTransform\":\"capitalize\"},\"children\":\"+977 xxx xxx xxxx\"}},{\"type\":\"h2\",\"props\":{\"id\":\"phone2\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":\"500\",\"textTransform\":\"capitalize\"},\"children\":\"+1 xxx xxx\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"padding\":\"1.25rem\",\"flexDirection\":\"column\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"width\":\"18rem\",\"height\":\"13rem\",\"backgroundColor\":\"#FFFFFF\",\"cursor\":\"pointer\",\"userSelect\":\"none\",\"boxShadow\":\"0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)\"},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"MdEmail\",\"props\":{\"size\":50}}}},{\"type\":\"h1\",\"props\":{\"id\":\"emailheading\",\"style\":{\"paddingTop\":\"1.25rem\",\"paddingBottom\":\"0.5rem\",\"fontSize\":\"1.5rem\",\"lineHeight\":\"2rem\",\"fontWeight\":\"700\",\"textTransform\":\"capitalize\"},\"children\":\"email\"}},{\"type\":\"h2\",\"props\":{\"id\":\"email1\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":\"500\",\"textTransform\":\"capitalize\"},\"children\":\"support@hamropatro.com\"}},{\"type\":\"h2\",\"props\":{\"id\":\"email2\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":\"500\",\"textTransform\":\"capitalize\"},\"children\":\"example123@gmail.com\"}}]}}]}}]}}");
        defaultContactSection.setCentered("{\"type\":\"div\",\"props\":{\"id\":\"contact-section\",\"style\":{\"height\":\"100vh\"},\"children\":{\"type\":\"div\",\"props\":{\"style\":{\"paddingTop\":\"6rem\"},\"children\":[\" \",{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingTop\":\"4rem\",\"paddingBottom\":\"4rem\",\"marginLeft\":\"4rem\",\"marginRight\":\"4rem\",\"gap\":\"8rem\",\"justifyContent\":\"space-evenly\",\"alignItems\":\"center\",\"borderRadius\":\"0.375rem\",\"backgroundColor\":\"#FFFFFF\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"paddingLeft\":\"1.25rem\",\"paddingRight\":\"1.25rem\",\"width\":\"20rem\"},\"children\":[{\"type\":\"h1\",\"props\":{\"id\":\"welcomeheading\",\"style\":{\"fontSize\":\"3rem\",\"lineHeight\":1,\"fontWeight\":600},\"children\":\"Get in touch with us!\"}},{\"type\":\"div\",\"props\":{\"style\":{\"paddingTop\":\"1.5rem\"},\"children\":[{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingTop\":\"0.75rem\",\"paddingBottom\":\"0.75rem\",\"gap\":\"1rem\",\"alignItems\":\"center\"},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"FaPhoneAlt\",\"props\":{\"size\":30}}}},{\"type\":\"p\",\"props\":{\"id\":\"phone1\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":500},\"children\":\"+977 XXX XXX XXXX\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingTop\":\"0.75rem\",\"paddingBottom\":\"0.75rem\",\"gap\":\"1rem\",\"alignItems\":\"center\"},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"MdEmail\",\"props\":{\"size\":30}}}},{\"type\":\"p\",\"props\":{\"id\":\"email1\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":500},\"children\":\"support@hamropatro.com\"}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"display\":\"flex\",\"paddingTop\":\"0.75rem\",\"paddingBottom\":\"0.75rem\",\"gap\":\"1rem\",\"alignItems\":\"center\"},\"children\":[{\"type\":\"h1\",\"props\":{\"style\":{\"color\":\"#E50914\"},\"children\":{\"type\":\"FaLocationDot\",\"props\":{\"size\":30}}}},{\"type\":\"p\",\"props\":{\"id\":\"address1\",\"style\":{\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":500},\"children\":\"Sifal-8, kathmandu 44600\"}}]}}]}}]}},{\"type\":\"div\",\"props\":{\"style\":{\"paddingLeft\":\"2.5rem\",\"paddingRight\":\"2.5rem\",\"width\":\"35%\"},\"children\":{\"type\":\"form\",\"props\":{\"style\":{\"display\":\"flex\",\"flexDirection\":\"column\",\"gap\":\"4rem\"},\"children\":[{\"type\":\"input\",\"props\":{\"type\":\"text\",\"name\":\"to_name\",\"placeholder\":\"Name\",\"style\":{\"paddingTop\":\"0.5rem\",\"paddingBottom\":\"0.5rem\",\"paddingLeft\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"border\":\"2px\",\"borderStyle\":\"solid\"}}},{\"type\":\"input\",\"props\":{\"type\":\"email\",\"name\":\"from_name\",\"placeholder\":\"Email\",\"style\":{\"paddingTop\":\"0.5rem\",\"paddingBottom\":\"0.5rem\",\"paddingLeft\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"border\":\"2px\",\"borderStyle\":\"solid\"}}},{\"type\":\"textarea\",\"props\":{\"name\":\"message\",\"placeholder\":\"Message\",\"style\":{\"paddingTop\":\"0.5rem\",\"paddingBottom\":\"0.5rem\",\"paddingLeft\":\"0.75rem\",\"borderRadius\":\"0.375rem\",\"borderColor\":\"2px\",\"maxHeight\":\"150px\",\"border\":\"2px\",\"borderStyle\":\"solid\"}}},{\"type\":\"input\",\"props\":{\"type\":\"submit\",\"value\":\"Send\",\"style\":{\"paddingTop\":\"0.5rem\",\"paddingBottom\":\"0.5rem\",\"borderRadius\":\"0.375rem\",\"fontSize\":\"1.125rem\",\"lineHeight\":\"1.75rem\",\"fontWeight\":600,\"color\":\"#FFFFFF\",\"backgroundColor\":\"#DC2626\"}}}]}}}}]}}]}}}}");
        defaultContactSection.setActive("Centered");
        return defaultContactSection;
    }

    private ContactResponse convertToContactResponse(ContactSection contactSection) {
        return ContactResponse.newBuilder()
                .setTile(contactSection.getTile())
                .setCentered(contactSection.getCentered())
                .setActive(contactSection.getActive())
                .build();
    }
}
