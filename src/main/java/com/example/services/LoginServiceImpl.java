

package com.example.services;

import com.example.LoginReply;
import com.example.LoginRequest;
import com.example.UserLoginGrpc;
import com.example.entity.UserEntity;
import com.example.security.AuthenticationService;
import com.example.security.TokenHandler;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Inject;

@GrpcService
public class LoginServiceImpl extends UserLoginGrpc.UserLoginImplBase {

    @Inject
    private TokenHandler tokenHandler;

    @Inject
    private AuthenticationService authenticationService;

    @Override
    public void login(LoginRequest request, StreamObserver<LoginReply> responseObserver) {
        try {
            // Authenticate the user
            UserEntity authenticatedUser = authenticationService.authenticateUser(request);

            if (authenticatedUser != null) {
                // Extract userId from the authenticated user
                Long userId = authenticatedUser.getUserId();

                // Generate an access token
                String token = tokenHandler.generateAccessToken(request);

                // Build the response
                LoginReply loginReply = LoginReply.newBuilder()
                        .setUserId(userId)  // Pass Long directly
                        .setAccessToken(token)
                        .setUserName(request.getUserName())
                        .build();

                // Send the response to the client
                responseObserver.onNext(loginReply);
                responseObserver.onCompleted();
                System.out.println("Login completed for userId: " + userId);
            } else {
                // Authentication failed
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Authentication Failed")
                        .asRuntimeException());
            }
        } catch (Exception e) {
            // Handle other exceptions
            System.out.println(e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Login failed")
                    .asRuntimeException());
        }
    }
}
