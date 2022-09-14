package org.ICIQ.eChempad.configurations.Utilities;

import org.ICIQ.eChempad.entities.Authority;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class PermissionIterator implements Iterator<Permission> {

    private Iterator<Permission> iterator;

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public Permission next() throws NoSuchElementException {
        return this.iterator.next();
    }
}
