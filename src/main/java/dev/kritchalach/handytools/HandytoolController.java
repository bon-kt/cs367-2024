package dev.kritchalach.handytools;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class HandytoolController {
    private final StorageRepository repository;

    HandytoolController(StorageRepository repository) {
        this.repository = repository;
    }

    // @GetMapping("/handytools")    
    // List<Storage> findAll() {
    //     return repository.findAll();
    // }
    @GetMapping("/handytools")    
    CollectionModel<EntityModel<Storage>> findAll() {
        List<EntityModel<Storage>> storages = repository.findAll().stream()
            .map(storage -> EntityModel.of(storage,
                linkTo(methodOn(HandytoolController.class).findOne(storage.getId())).withSelfRel(),
                linkTo(methodOn(HandytoolController.class).findAll()).withRel("handytools")))
            .collect(Collectors.toList());
        return CollectionModel.of(storages, linkTo(methodOn(HandytoolController.class).findAll()).withSelfRel());
    }

    @GetMapping("/handytools/{id}")
    // EntityModel<Map<String, Storage>> findOne(@PathVariable Long id) {
    EntityModel<Storage> findOne(@PathVariable Long id) {
        Storage storage = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no handy tool by given id"));
        // Map<String, Storage> handytool = Map.of("handytool", storage);
        // return EntityModel.of( handytool,
        return EntityModel.of( storage,
            linkTo(methodOn(HandytoolController.class).findOne(id)).withSelfRel(),
            linkTo(methodOn(HandytoolController.class).findAll()).withRel("handytools"));
    }
    
    // @GetMapping("/handytools/{id}")
    // Storage findOne(@PathVariable Long id) {
    //     Optional<Storage> handytool =  repository.findById(id);
    //     if (handytool.isEmpty()) {
    //         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no handy tool by given id");
    //     }
    //     return handytool.get();
    // }
    
    @PostMapping("/handytools")
    Storage newHandyTool(@RequestBody Storage handytool) {        
        return repository.save(handytool);
    }
    
    @PutMapping("/handytools/{id}")
    Storage saveHandyTool(@RequestBody Storage newHandyTool, @PathVariable Long id) {
        return repository.findById(id).map(handytool -> {
            handytool.setBorrowed(newHandyTool.getBorrowed());
            handytool.setBorrowerName(newHandyTool.getBorrowerName());
            handytool.setLocationName(newHandyTool.getLocationName());
            handytool.setOwnerName(newHandyTool.getOwnerName());
            handytool.setToolDetail(newHandyTool.getToolDetail());
            return repository.save(handytool);
        }).orElseGet(() -> {
            return repository.save(newHandyTool);
        });
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/handytools/{id}")
    void deleteHandyTool(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
