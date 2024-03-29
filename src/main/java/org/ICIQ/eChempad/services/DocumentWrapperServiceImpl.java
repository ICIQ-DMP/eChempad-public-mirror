/*
 * eChempad is a suite of web services oriented to manage the entire
 * data life-cycle of experiments and assays from Experimental
 * Chemistry and related Science disciplines.
 *
 * Copyright (C) 2021 - present Institut Català d'Investigació Química (ICIQ)
 *
 * eChempad is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.ICIQ.eChempad.services;

import org.ICIQ.eChempad.configurations.converters.DocumentWrapperConverter;
import org.ICIQ.eChempad.entities.DocumentWrapper;
import org.ICIQ.eChempad.entities.genericJPAEntities.*;
import org.ICIQ.eChempad.exceptions.NotEnoughAuthorityException;
import org.ICIQ.eChempad.exceptions.ResourceNotExistsException;
import org.ICIQ.eChempad.services.genericJPAServices.DocumentService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentWrapperServiceImpl<T extends Entity, S extends Serializable> implements DocumentWrapperService<DocumentWrapper, UUID> {

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
    public DocumentWrapper addDocumentToExperiment(DocumentWrapper documentWrapper, UUID experiment_uuid) {
        // Transform to DB entity
        org.ICIQ.eChempad.entities.genericJPAEntities.Document document = this.documentWrapperConverter.convertToDatabaseColumn(documentWrapper);

        // Save DB entity and retrieve instance. Warning! The Blob has already been read. If you try to read the Blob
        // again you will get a java.sql.SQLException: could not reset reader
        org.ICIQ.eChempad.entities.genericJPAEntities.Document documentDatabase = this.documentService.addDocumentToContainer(document, experiment_uuid);

        // Reconvert to Transport entity and return it
        //TODO Change return of this method, so we do not need to reconvert, since a silent error is thrown on server side because it cannot reread the stream of the BLOB
        documentWrapper = this.documentWrapperConverter.convertToEntityAttribute(documentDatabase);
        return documentWrapper;
    }

    @Override
    public Set<DocumentWrapper> getDocumentsFromExperiment(UUID experiment_uuid) {
        return this.documentService.getDocumentsFromContainer(experiment_uuid)
                .stream()
                .map(this.documentWrapperConverter::convertToEntityAttribute)
                .collect(Collectors.toSet());
    }

    @Override
    public Resource getDocumentData(UUID document_uuid) throws ResourceNotExistsException, NotEnoughAuthorityException {

        DocumentWrapper d = this.documentWrapperConverter.convertToEntityAttribute(this.documentService.getById(document_uuid));

        Resource inputStreamResource = null;
        try {
            inputStreamResource = new InputStreamResource(d.getFile().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStreamResource;
    }

    // Delegated methods to Document Service, which in turns delegates to JPA repository.
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
    public <S1 extends DocumentWrapper> S1 save(S1 entity) {
        org.ICIQ.eChempad.entities.genericJPAEntities.Document document = this.documentWrapperConverter.convertToDatabaseColumn(entity);

        // Internally the BLOB is consumed, so if we use the same instance to return an exception will be thrown
        // java.sql.SQLException: could not reset reader
        org.ICIQ.eChempad.entities.genericJPAEntities.Document documentDatabase = this.documentService.save(document);

        // This seems silly, but is necessary to update the Blob and its InputStream
        Optional<org.ICIQ.eChempad.entities.genericJPAEntities.Document> documentDatabaseOptional = this.documentService.findById((UUID) documentDatabase.getId());

        return documentDatabaseOptional.map(value -> (S1) this.documentWrapperConverter.convertToEntityAttribute(value)).orElse(null);
    }

    @Override
    public Optional<DocumentWrapper> findById(UUID uuid) {

        Optional<org.ICIQ.eChempad.entities.genericJPAEntities.Document> opt = this.documentService.findById(uuid);
        org.ICIQ.eChempad.entities.genericJPAEntities.Document document;
        if (opt.isPresent())
        {
            document = opt.get();
        }
        else
        {
            return Optional.empty();  // TODO throw except
        }

        return Optional.of(this.documentWrapperConverter.convertToEntityAttribute(document));
    }

    @Override
    public List<DocumentWrapper> findAllById(Iterable<UUID> uuids) {
        return this.documentService.findAllById(uuids).stream().map(
                this.documentWrapperConverter::convertToEntityAttribute

        ).collect(Collectors.toList());
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> saveAll(Iterable<S1> entities) {
        List<org.ICIQ.eChempad.entities.genericJPAEntities.Document> documentList = new LinkedList<>();
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
        List<org.ICIQ.eChempad.entities.genericJPAEntities.Document> documentList = new LinkedList<>();
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
        List<org.ICIQ.eChempad.entities.genericJPAEntities.Document> documentList = new LinkedList<>();
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
        List<org.ICIQ.eChempad.entities.genericJPAEntities.Document> documentList = new LinkedList<>();
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
        List<org.ICIQ.eChempad.entities.genericJPAEntities.Document> list = this.documentService.findAll(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe())));
        return (List<S1>) list.stream().map(
                d ->
                {
                    return this.documentWrapperConverter.convertToEntityAttribute(d);
                }
        ).collect(Collectors.toList());
    }

    @Override
    public <S1 extends DocumentWrapper> List<S1> findAll(Example<S1> example, Sort sort) {
        List<org.ICIQ.eChempad.entities.genericJPAEntities.Document> list = this.documentService.findAll(Example.of(this.documentWrapperConverter.convertToDatabaseColumn(example.getProbe())), sort);
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

    @Override
    public @NotNull DocumentWrapper getOne(@NotNull UUID uuid) {
        return null;
    }
}
