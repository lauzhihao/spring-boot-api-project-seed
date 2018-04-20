package com.xykj.koala.core;

import java.util.function.Supplier;

/**
 * @author liuzhihao
 * @date 2018/4/16
 */
public class InsightExceptions {

    public static Supplier<ServiceException> permissionDeniedExceptionSupplier = () -> new ServiceException("Permission Denied.");

    public static Supplier<ServiceException> targetNotFoundExceptionSupplier = () -> new ServiceException("Target Not Found.");

    public static Supplier<ServiceException> whoAreYouExceptionSupplier = () -> new ServiceException("Who Are You?");

    public static Supplier<ServiceException> illegalArgumentExceptionSupplier = () -> new ServiceException("Invalid Argument.");

}
