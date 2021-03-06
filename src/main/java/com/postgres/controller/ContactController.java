package com.postgres.controller;

import com.postgres.exception.*;
import com.postgres.model.Address;
import com.postgres.model.Contact;
import com.postgres.service.ContactService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/v1")
public class ContactController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int ROWPERPAGE = 5;

    @Autowired
    private ContactService contactService;


//    @ApiOperation(value = "Find contact by ID", notes = "Returns a single contact", tags = {"contact"})
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "successful operation", response = Contact.class),
//            @ApiResponse(code = 404, message = "Contact not found")})
//    @GetMapping(value = "/findByID/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Contact> findById(@ApiParam(name = "contactId", value = "informe o ID. não pode ser null.",
//                    example = "1",
//                    required = true)
//            @PathVariable long id) {
//        try {
//            Contact contact = contactService.findById(id);
//            return new ResponseEntity<>(contact, HttpStatus.OK);
//        } catch (ResourceNotFoundException ex) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // return 404, with null body
//        }
//    }

    @ApiOperation(value = "Find contact by ID", notes = "Returns a single contact", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Contact.class),
            @ApiResponse(code = 404, message = "Contact not found")})
    @GetMapping(value = "/findByID/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> findByID(@ApiParam(name = "contactId", value = "informe o ID. não pode ser null.",
            example = "1", required = true) @PathVariable long id) throws ResourceNotFoundException {
        Optional<Contact> contact = Optional.ofNullable(contactService.findById(id));
        if (contact.isPresent()) {
            return new ResponseEntity<>(contact.get(), HttpStatus.OK);
        } else {
            throw new RecordNotFoundException();
        }
    }


    @ApiOperation(value = "Procurar pelo nome", notes = "Name search by %name% format", tags = {"contact"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "successful operation", response = List.class)})
    @RequestMapping(method = RequestMethod.GET, path = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Contact>> findAll(@ApiParam(name = "contactId", value = "Page number, default is 1",
            example = "1", required = false) @RequestParam(value = "page", defaultValue = "1") int pageNumber,
                                                 @ApiParam("Digite o nome para procurar.") @RequestParam(required = false)
                                                         String name, @RequestParam(required = false) String email) {
        if (StringUtils.isEmpty(name)) {
            return ResponseEntity.ok(contactService.findAll(pageNumber, ROWPERPAGE));
        } else {
            return ResponseEntity.ok(contactService.findAllByName(name, pageNumber, ROWPERPAGE));
        }
    }

    @ApiOperation(value = "Adicionar um novo contato", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Contato criado"),
            @ApiResponse(code = 400, message = "Entradas inválidas"),
            @ApiResponse(code = 409, message = "Contato já existente")})
    @PostMapping(value = "/insert")
    public ResponseEntity<Contact> save(@ApiParam("Contact to add. Cannot null or empty.")
                                        @Valid @RequestBody Contact contact) throws URISyntaxException {
        try {
            Contact newContact = contactService.save(contact);
            return ResponseEntity.created(new URI("/api/v1/findByID/" + newContact.getId()))
                    .body(contact);
        } catch (ResourceAlreadyExistsException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (BadResourceException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ApiOperation(value = "Update an existing contact", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Contact not found"),
            @ApiResponse(code = 405, message = "Validation exception")})
    @PutMapping(value = "/update/{contactId}")
    public ResponseEntity<Contact> update(@ApiParam(name = "contactId", value = "Id of the contact to be update. Cannot be empty.", example = "1", required = true)
                                          @PathVariable long contactId, @ApiParam("Contact to update. Cannot null or empty.") @Valid @RequestBody Contact contact, BindingResult bindingResult) throws ResourceNotFoundException, BadResourceException {

        Optional<Contact> currentContact = Optional.ofNullable(contactService.findById(contactId));
        BindingErrorsResponse errors = new BindingErrorsResponse();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors() || (contact == null)) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
        if (!currentContact.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        contactService.update(contact);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @ApiOperation(value = "Update an existing contact's address", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 404, message = "Contact not found")})
    @PatchMapping("/updateAddress/{contactId}")
    public ResponseEntity<Void> updateAddress(
            @ApiParam(name = "contactId",
                    value = "Id of the contact to be update. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long contactId,
            @ApiParam("Contact's address to update.")
            @RequestBody Address address) {
        try {
            contactService.updateAddress(contactId, address);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Deletes a contact", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 404, message = "Contact not found")})
    @DeleteMapping(path = "/delete/{contactId}")
    public ResponseEntity<Void> delete(@ApiParam(name = "contactId", value = "Id of the contact to be delete. Cannot be empty.", example = "1",
            required = true) @PathVariable long contactId) {

        try {
            contactService.delete(contactId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}