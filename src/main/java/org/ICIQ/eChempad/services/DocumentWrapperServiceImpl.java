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
import org.springframework.data.domain.*;
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
    public DocumentWrapper addDocumentToExperiment(DocumentWrapper document, UUID experiment_uuid) {
        return this.documentWrapperConverter.convertToEntityAttribute(this.documentService.addDocumentToExperiment(this.documentWrapperConverter.convertToDatabaseColumn(document), experiment_uuid));
    }

    @Override
    public Set<DocumentWrapper> getDocumentsFromExperiment(UUID experiment_uuid) {
        return this.documentService.getDocumentsFromExperiment(experiment_uuid).stream().map(
                document ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(document);
                }
        ).collect(Collectors.toSet());
    }

    @Override
    public Resource getDocumentData(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {
        return this.documentService.getDocumentData(document_uuid);
    }

    // Delegated methods to Document Service, which in turns delegates to JPA repository

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
        return (S1) this.documentWrapperConverter.convertToEntityAttribute(this.documentService.save(this.documentWrapperConverter.convertToDatabaseColumn(entity)));
    }

    @Override
    public Optional<DocumentWrapper> findById(UUID uuid) {
        return Optional.of(this.documentWrapperConverter.convertToEntityAttribute(this.documentService.findById(uuid).get()));
    }

    @Override
    public boolean existsById(UUID uuid) {
        return this.documentService.existsById(uuid);
    }

    @Override
    public long count() {
        return this.documentService.count();
    }

    @Override
    public void deleteById(UUID uuid) {
        this.documentService.deleteById(uuid);
    }

    @Override
    public void delete(DocumentWrapper entity) {
        this.documentService.delete(this.documentWrapperConverter.convertToDatabaseColumn(entity));
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        this.documentService.deleteAllById(uuids);
    }

    @Override
    public void deleteAll(Iterable<? extends DocumentWrapper> entities) {
        List<Document> documentList = new LinkedList<>();
        for (DocumentWrapper entity : entities) {
            documentList.add(this.documentWrapperConverter.convertToDatabaseColumn(entity));
        }

        this.documentService.deleteAll(documentList);
    }

    @Override
    public void deleteAll() {
        this.documentService.deleteAll();
    }


    @Override
    public <S1 extends DocumentWrapper> S1 saveAndFlush(S1 entity) {
        return (S1) this.documentWrapperConverter.convertToEntityAttribute(this.documentService.saveAndFlush(this.documentWrapperConverter.convertToDatabaseColumn(entity)));
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> saveAllAndFlush(Iterable<S1> entities) {
        List<Document> documentList = new LinkedList<>();
        for (S1 entity : entities) {
            documentList.add(this.documentWrapperConverter.convertToDatabaseColumn(entity));
        }

        documentList = this.documentService.saveAllAndFlush(documentList);

        return (List<S1>) documentList.stream().map(
                d ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(d);
                }
        ).collect(Collectors.toList());
    }

    @Override
    public void deleteAllInBatch(Iterable<DocumentWrapper> entities) {
        List<Document> documentList = new LinkedList<>();
        for (DocumentWrapper entity : entities) {
            documentList.add(this.documentWrapperConverter.convertToDatabaseColumn(entity));
        }

        this.documentService.deleteAllInBatch(documentList);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
        this.documentService.deleteAllByIdInBatch(uuids);
    }

    @Override
    public void deleteAllInBatch() {
        this.documentService.deleteAllInBatch();
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> findAll(Example<S1> example) {
        List<Document> list = this.documentService.findAll(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe())));
        return (List<S1>) list.stream().map(
                d ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(d);
                }
        ).collect(Collectors.toList());
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> findAll(Example<S1> example, Sort sort) {
        List<Document> list = this.documentService.findAll(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe())), sort);
        return (List<S1>) list.stream().map(
                d ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(d);
                }
        ).collect(Collectors.toList());
    }

    @Override
    public Page<DocumentWrapper> findAll(Pageable pageable) {
        return new PageImpl<>(this.documentService.findAll(pageable).stream().map(
                d ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(d);
                }
        ).collect(Collectors.toList()));
    }


    @Override
    public <S extends DocumentWrapper> Optional<S> findOne(Example<S> example) {
        return (Optional<S>) Optional.of(this.documentWrapperConverter.convertToEntityAttribute(this.documentService.findOne(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe()))).get()));
    }

    @Override
    public <S extends DocumentWrapper> Page<S> findAll(Example<S> example, Pageable pageable) {
        return (Page<S>) new PageImpl<DocumentWrapper>(this.documentService.findAll(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe())), pageable).stream().map(
                d ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(d);
                }
        ).collect(Collectors.toList()));
    }

    @Override
    public <S extends DocumentWrapper> long count(Example<S> example) {
        return this.documentService.count(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe())));
    }

    @Override
    public <S extends DocumentWrapper> boolean exists(Example<S> example) {
        return this.documentService.exists(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe())));
    }

}
