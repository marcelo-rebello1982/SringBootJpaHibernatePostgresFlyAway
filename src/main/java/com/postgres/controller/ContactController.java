package com.postgres.controller;

import com.postgres.exception.BadResourceException;
import com.postgres.exception.ResourceAlreadyExistsException;
import com.postgres.exception.ResourceNotFoundException;
import com.postgres.model.Address;
import com.postgres.model.Contact;
import com.postgres.service.ContactService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class ContactController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int ROW_PER_PAGE = 5;

    @Autowired
    private ContactService contactService;

    @ApiOperation(value = "Find Contacts by name", notes = "Name search by %name% format", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = List.class)})
    @GetMapping(value = "/contacts")
    public ResponseEntity<List<Contact>> findAll(
            @ApiParam(name = "contactId",
                    value = "Page number, default is 1",
                    example = "1",
                    required = false) @RequestParam(value = "page", defaultValue = "1") int pageNumber,
            @ApiParam("Name of the contact for search.") @RequestParam(required = false) String name) {
        if (StringUtils.isEmpty(name)) {
            return ResponseEntity.ok(contactService.findAll(pageNumber, ROW_PER_PAGE));
        } else {
            return ResponseEntity.ok(contactService.findAllByName(name, pageNumber, ROW_PER_PAGE));
        }
    }

    @ApiOperation(value = "Find contact by ID", notes = "Returns a single contact", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation", response = Contact.class),
            @ApiResponse(code = 404, message = "Contact not found")})
    @GetMapping(value = "/contacts/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> findContactById(
            @ApiParam(name = "contactId",
                    value = "Id of the contact to be obtained. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long contactId) {
        try {
            Contact contact = contactService.findById(contactId);
            return ResponseEntity.ok(contact);  // return 200, with json body
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // return 404, with null body
        }
    }

    @ApiOperation(value = "Add a new contact", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Contact created"),
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 409, message = "Contact already exists")})
    @PostMapping(value = "/contacts")
    public ResponseEntity<Contact> addContact(
            @ApiParam("Contact to add. Cannot null or empty.")
            @Valid @RequestBody Contact contact)
            throws URISyntaxException {
        try {
            Contact newContact = contactService.save(contact);
            return ResponseEntity.created(new URI("/api/contacts/" + newContact.getId()))
                    .body(contact);
        } catch (ResourceAlreadyExistsException ex) {
            // log exception first, then return Conflict (409)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
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
    @PutMapping(value = "/contacts/{contactId}")
    public ResponseEntity<Contact> updateContact(
            @ApiParam(name = "contactId",
                    value = "Id of the contact to be update. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long contactId,
            @ApiParam("Contact to update. Cannot null or empty.")
            @Valid @RequestBody Contact contact) {
        try {
            contact.setId(contactId);
            contactService.update(contact);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadResourceException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ApiOperation(value = "Update an existing contact's address", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 404, message = "Contact not found")})
    @PatchMapping("/contacts/{contactId}")
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
            // log exception first, then return Not Found (404)
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @ApiOperation(value = "Deletes a contact", tags = {"contact"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful operation"),
            @ApiResponse(code = 404, message = "Contact not found")})
    @DeleteMapping(path = "/contacts/{contactId}")
    public ResponseEntity<Void> deleteContactById(
            @ApiParam(name = "contactId",
                    value = "Id of the contact to be delete. Cannot be empty.",
                    example = "1",
                    required = true)
            @PathVariable long contactId) {
        try {
            contactService.deleteById(contactId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
