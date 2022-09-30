package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.converters.DocumentWrapperConverter;
import org.ICIQ.eChempad.configurations.security.ACL.AclServiceCustomImpl;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.repositories.genericJPARepositories.ExperimentRepository;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.ICIQ.eChempad.services.genericJPAServices.ExperimentService;
import org.ICIQ.eChempad.services.genericJPAServices.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentWrapperServiceImpl<T extends JPAEntityImpl, S extends Serializable> implements DocumentWrapperService<DocumentWrapper, UUID> {

    private final DocumentService<T, S> documentService;
    private final DocumentWrapperConverter documentWrapperConverter;

    @Autowired
    public DocumentWrapperServiceImpl(DocumentService<T, S> documentService, DocumentWrapperConverter documentWrapperConverter) {
        this.documentService = documentService;
        this.documentWrapperConverter = documentWrapperConverter;
    }

    @Override
    public Class<DocumentWrapper> getEntityClass() {
        return DocumentWrapper.class;
    }


    @Override
    public List<DocumentWrapper> findAll() {
        return this.documentService.findAll().stream().map(
                this.documentWrapperConverter::convertToEntityAttribute

        ).collect(Collectors.toList());
    }

    @Override
    public List<DocumentWrapper> findAll(Sort sort) {
        return this.documentService.findAll(sort).stream().map(
                this.documentWrapperConverter::convertToEntityAttribute

        ).collect(Collectors.toList());
    }

    @Override
    public List<DocumentWrapper> findAllById(Iterable<UUID> uuids) {
        return this.documentService.findAllById(uuids).stream().map(
                this.documentWrapperConverter::convertToEntityAttribute

        ).collect(Collectors.toList());
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> saveAll(Iterable<S1> entities) {
        List<Document> documentList = new LinkedList<>();
        for (S1 entity : entities) {
            documentList.add(this.documentWrapperConverter.convertToDatabaseColumn(entity));
        }

        documentList = this.documentService.saveAll(documentList);

        return (List<S1>) documentList.stream().map(
                d ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(d);
                }
        ).collect(Collectors.toList());

    }

    @Override
    public void flush() {
        this.documentService.flush();
    }

    @Override
    public DocumentWrapper getById(UUID uuid) {
        return this.documentWrapperConverter.convertToEntityAttribute(this.documentService.getById(uuid));
    }

    @Override
    public <S1 extends DocumentWrapper> S1 save(S1 entity) {
        return null;
    }

    @Override
    public Optional<DocumentWrapper> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void delete(DocumentWrapper entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends DocumentWrapper> entities) {

    }

    @Override
    public void deleteAll() {

    }


    @Override
    public <S1 extends DocumentWrapper> S1 saveAndFlush(S1 entity) {
        return null;
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> saveAllAndFlush(Iterable<S1> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<DocumentWrapper> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> findAll(Example<S1> example) {
        return null;
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> findAll(Example<S1> example, Sort sort) {
        return null;
    }

    @Override
    public Page<DocumentWrapper> findAll(Pageable pageable) {
        return null;
    }


    @Override
    public <S extends DocumentWrapper> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends DocumentWrapper> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends DocumentWrapper> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends DocumentWrapper> boolean exists(Example<S> example) {
        return false;
    }
}
