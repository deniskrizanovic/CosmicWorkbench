package com.fp.web;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Iterator;
import java.util.Set;

public class DebugUtilities
{
    public DebugUtilities()
    {
    }

    public void writeRequest(MultipartHttpServletRequest request)
    {
        Set<String> params = request.getParameterMap().keySet();
        for (Iterator<String> iterator = params.iterator(); iterator.hasNext(); )
        {
            String next = iterator.next();

            System.out.println("key=" + next + " value= " + request.getParameter(next));

        }
    }
}