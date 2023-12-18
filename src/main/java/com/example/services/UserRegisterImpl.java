package com.example.services;

import com.example.UserReply;
import com.example.UserRequest;
import com.example.UserSignUpGrpc;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.security.BcryptEncoderService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.inject.Inject;


public class UserRegisterImpl extends UserSignUpGrpc.UserSignUpImplBase {

    @Inject
    public UserRepository userRepository;
    @Inject
    public BcryptEncoderService bcryptEncoderService;

    @Override
    public void register(UserRequest request, StreamObserver<UserReply> responseObserver) {

        try{
            UserReply userReply = UserReply.newBuilder()
                    .setUserName(request.getUserName())
                    .setEmail(request.getEmail())
                    .build();

            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(request.getEmail());
            userEntity.setUserName(request.getUserName());
            userEntity.setPassword(bcryptEncoderService.hashPassword(request.getPassword()));
            userRepository.save(userEntity);
            responseObserver.onNext(userReply);
            responseObserver.onCompleted();
            System.out.println(userReply);
        }
        catch(Exception e){
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());

        }


    }
}
