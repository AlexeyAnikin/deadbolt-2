/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.objectify.deadbolt.java.actions;

import be.objectify.deadbolt.core.models.RoleHolder;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.utils.RequestUtils;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Implements the {@link RoleHolderNotPresent} functionality, i.e. the
 * {@link be.objectify.deadbolt.core.models.RoleHolder} provided by the {@link be.objectify.deadbolt.java.DeadboltHandler}
 * must be null to have access to the resource.
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
public class RoleHolderNotPresentAction extends AbstractDeadboltAction<RoleHolderNotPresent>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public Result execute(Http.Context ctx) throws Throwable
    {
        Result result;
        if (isActionUnauthorised(ctx))
        {
            result = onAccessFailure(getDeadboltHandler(configuration.handler()),
                                     configuration.content(),
                                     ctx);
        }
        else
        {
            DeadboltHandler deadboltHandler = getDeadboltHandler(configuration.handler());
            RoleHolder roleHolder = getRoleHolder(ctx,
                                                  deadboltHandler);

            if (roleHolder == null)
            {
                markActionAsAuthorised(ctx);
                result = delegate.call(ctx);
            }
            else
            {
                markActionAsUnauthorised(ctx);
                result = onAccessFailure(deadboltHandler,
                                         configuration.content(),
                                         ctx);
            }
        }

        return result;
    }

    /**
     * Gets the {@link RoleHolder} from the {@link DeadboltHandler}, and logs an error if it's not present. Note that
     * at least one actions ({@link Unrestricted} does not not require a RoleHolder to be present.
     *
     * @param ctx             the request context
     * @param deadboltHandler the Deadbolt handler
     * @return the RoleHolder, if any
     */
    @Override
    protected RoleHolder getRoleHolder(Http.Context ctx,
                                       DeadboltHandler deadboltHandler)
    {
        return RequestUtils.getRoleHolder(deadboltHandler,
                                          ctx);
    }
}
