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
package be.objectify.deadbolt.core;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.RoleHolder;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This carries out static (i.e. non-dynamic) checks.
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
public class DeadboltAnalyzer
{
    /**
     * Checks if the roleHolder has all the role names.  In other words, this gives AND support.
     *
     * @param roleHolder the role holder
     * @param roleNames  the role names.  Any role name starting with ! will be negated.
     * @return true if the role holder meets the restrictions (so access will be allowed), otherwise false
     */
    public static boolean checkRole(RoleHolder roleHolder,
                                    String[] roleNames)
    {
        boolean roleOk = true;
        if (!hasAllRoles(roleHolder,
                         roleNames))
        {
            roleOk = false;
        }
        return roleOk;
    }

    public static boolean hasAllRoles(RoleHolder roleHolder,
                                      String[] roleNames)
    {
        boolean hasRole = false;
        if (roleHolder != null)
        {
            List<? extends Role> roles = roleHolder.getRoles();

            if (roles != null)
            {
                List<String> heldRoles = new ArrayList<String>();
                for (Role role : roles)
                {
                    if (role != null)
                    {
                        heldRoles.add(role.getRoleName());
                    }
                }

                boolean roleCheckResult = true;
                for (int i = 0; roleCheckResult && i < roleNames.length; i++)
                {
                    boolean invert = false;
                    String roleName = roleNames[i];
                    if (roleName.startsWith("!"))
                    {
                        invert = true;
                        roleName = roleName.substring(1);
                    }
                    roleCheckResult = heldRoles.contains(roleName);

                    if (invert)
                    {
                        roleCheckResult = !roleCheckResult;
                    }
                }
                hasRole = roleCheckResult;
            }
        }
        return hasRole;
    }

    public static boolean checkRegexPattern(RoleHolder roleHolder,
                                            Pattern pattern)
    {
        boolean roleOk = false;
        if (roleHolder != null)
        {
            List<? extends Permission> permissions = roleHolder.getPermissions();
            if (permissions != null)
            {
                for (Iterator<? extends Permission> iterator = permissions.iterator(); !roleOk && iterator.hasNext(); )
                {
                    Permission permission = iterator.next();
                    roleOk = pattern.matcher(permission.getValue()).matches();
                }
            }
        }

        return roleOk;
    }

    public static boolean checkPatternEquality(RoleHolder roleHolder,
                                               String patternValue)
    {
        boolean roleOk = false;
        if (roleHolder != null)
        {
            List<? extends Permission> permissions = roleHolder.getPermissions();
            if (permissions != null)
            {
                for (Iterator<? extends Permission> iterator = permissions.iterator(); !roleOk && iterator.hasNext(); )
                {
                    Permission permission = iterator.next();
                    roleOk = patternValue.equals(permission.getValue());
                }
            }
        }

        return roleOk;
    }

    public static boolean checkCustomPattern(RoleHolder roleHolder,
                                             DeadboltHandler handler,
                                             Http.Context context,
                                             String value)
    {
        boolean patternOk = false;

        DynamicResourceHandler dynamicResourceHandler = handler.getDynamicResourceHandler(context);
        if (dynamicResourceHandler == null)
        {
            throw new RuntimeException("A custom permission type is specified but no dynamic resource handler is provided");
        }
        else
        {
            patternOk = dynamicResourceHandler.checkPermission(value,
                                                               handler,
                                                               context);
        }

        return patternOk;
    }
}
