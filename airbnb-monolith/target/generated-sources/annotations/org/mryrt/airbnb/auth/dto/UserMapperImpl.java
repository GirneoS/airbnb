package org.mryrt.airbnb.auth.dto;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.mryrt.airbnb.auth.model.Role;
import org.mryrt.airbnb.auth.model.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-13T11:58:34+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getId() );
        userDto.username( user.getUsername() );
        userDto.email( user.getEmail() );
        Set<Role> set = user.getRoles();
        if ( set != null ) {
            userDto.roles( new LinkedHashSet<Role>( set ) );
        }
        userDto.createdAt( user.getCreatedAt() );
        userDto.updatedAt( user.getUpdatedAt() );

        return userDto.build();
    }
}
