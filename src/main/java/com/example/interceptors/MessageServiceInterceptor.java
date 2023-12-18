package com.example.interceptors;

import com.example.exceptions.InvalidTokenException;
import com.example.security.Constants;
import com.example.security.TokenValidator;
import io.grpc.*;
import io.jsonwebtoken.Claims;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class MessageServiceInterceptor implements ServerInterceptor {
    @Inject
    private TokenValidator tokenValidator;
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata headers, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        MethodDescriptor<ReqT, RespT> methodDescriptor = serverCall.getMethodDescriptor();

        if (methodDescriptor.getBareMethodName().equals("message")) {
            System.out.println(headers);
            String authorizationHeader = headers.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));
            System.out.println(authorizationHeader);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                // Handle missing or invalid authorization header
                serverCall.close(Status.UNAUTHENTICATED.withDescription("Invalid or missing access token"), headers);

            }
            String accessToken = authorizationHeader.substring(7);
            System.out.println(methodDescriptor.getBareMethodName());

            try {
                // Validate the access token and extract claims
                Claims claims = tokenValidator.validateToken(accessToken, Constants.JWT_SIGNING_KEY);


                return serverCallHandler.startCall(serverCall, headers);
            } catch (InvalidTokenException e) {

                serverCall.close(Status.UNAUTHENTICATED.withDescription("Invalid or expired access token"), headers);
                return new ServerCall.Listener<>() {
                };
            }
        }
        return serverCallHandler.startCall(serverCall, headers);

    }
}
