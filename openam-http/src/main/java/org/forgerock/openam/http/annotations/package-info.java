/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */

/**
 * This package contains annotations that can be used to declare HTTP methods for being handled by
 * methods on a class. The {@link org.forgerock.openam.http.annotations.Endpoints} class provides a method
 * that will convert an object with methods with these annotations on them into a
 * {@link org.forgerock.http.Handler} that can be bound to a router or returned as the application
 * handler.
 * @since 13.0.0
 */
package org.forgerock.openam.http.annotations;
