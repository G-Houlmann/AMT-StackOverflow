package io.stackunderflow.flow.application.identitymgmt;

import io.stackunderflow.flow.application.identitymgmt.authenticate.AuthenticateCommand;
import io.stackunderflow.flow.application.identitymgmt.authenticate.AuthenticationFailedException;
import io.stackunderflow.flow.application.identitymgmt.authenticate.CurrentUserDTO;
import io.stackunderflow.flow.application.identitymgmt.login.RegisterCommand;
import io.stackunderflow.flow.application.identitymgmt.login.RegistrationFailedException;
import io.stackunderflow.flow.domain.person.IPersonRepository;
import io.stackunderflow.flow.domain.person.Person;

public class IdentityManagementFacade {

    private IPersonRepository personRepository;

    public IdentityManagementFacade(IPersonRepository personRepository){
        this.personRepository = personRepository;
    }

    public void register(RegisterCommand command) throws RegistrationFailedException {

        //Check if another user has already the same username
        //TODO ET l'email alors ???
        Person existingPersonWithSameUsername = personRepository.findByUsername(command.getUsername()).orElse(null);

        if(existingPersonWithSameUsername != null){
            throw new RegistrationFailedException("Username already exist!");
        }

        try {
            Person newPerson = Person.builder()
                    .username(command.getUsername())
                    .firstname(command.getFirstname())
                    .lastname(command.getLastname())
                    .email(command.getEmail())
                    .clearTextPassword(command.getClearTextPassword())
                    .build();
            personRepository.save(newPerson);
        }catch (Exception e){
            throw new RegistrationFailedException(e.getMessage());
        }
    }

    public CurrentUserDTO authenticate(AuthenticateCommand command) throws AuthenticationFailedException {
        Person person = personRepository.findByUsername(command.getUsername())
                .orElseThrow(()-> new AuthenticationFailedException("User not found!"));

        boolean success = person.authenticate(command.getClearTextPassword());
        if(!success)
            throw new AuthenticationFailedException("Password verification failed!");

        CurrentUserDTO  currentUser = CurrentUserDTO.builder()
                .username(person.getUsername())
                .firstname(person.getFirstname())
                .lastname(person.getLastname())
                .email(person.getEmail())
                .build();

        return currentUser;
    }

}