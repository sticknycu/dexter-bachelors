package ro.sticknycu.bachelors.dexter.infra

import ro.sticknycu.bachelors.dexter.chats.ChatToUserMappingsHolder
import org.springframework.stereotype.Service

//    public boolean isUserPartOfChat(final UUID chatId) {
//        return ReactiveSecurityContextHolder.getContext()
//                .switchIfEmpty(Mono.error(new IllegalStateException("EMPTY CONTEXT")))
//                .log()
//                .flatMap(securityContext -> {
//                    log.info("mapping");
//                    return (Mono<Jwt>) securityContext.getAuthentication().getPrincipal();
//                })
//                .map(jwt -> JwtUtil.extractUserName(jwt))
////                .map(jwtMono -> Jwtutil.extractUserName(jwtMono))
//                .flatMap(o -> chatToUserMappingsHolder.getUserChatRooms(Mono.just(o)))
//                .map(uuids -> uuids.contains(chatId))
////                .share()
//                .block();
//        final var isAuthorized = userName.log().map(authentication -> authentication.getPrincipal().toString())
//                .log()
//                .flatMap(o -> chatToUserMappingsHolder.getUserChatRooms(Mono.just(o)))
//                .map(uuids -> uuids.contains(chatId))
//                .share()
//                .block();
////        final var isAuthorized = chatToUserMappingsHolder.getUserChatRooms(Mono.just(userName))
////                .map(uuids -> uuids.contains(chatId))
////                .share()
////                .block();
////        log.info("User {} is authorized : {}", userName, isAuthorized);
//        return isAuthorized;
//    }
@Service
class PermissionEvaluator //https://github.com/spring-projects/spring-security/issues/9401
    (private val chatToUserMappingsHolder: ChatToUserMappingsHolder)
