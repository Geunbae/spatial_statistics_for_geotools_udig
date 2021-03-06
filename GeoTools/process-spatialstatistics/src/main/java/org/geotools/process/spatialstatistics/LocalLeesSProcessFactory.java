/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2014, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.process.spatialstatistics;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.geotools.data.Parameter;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.process.Process;
import org.geotools.process.spatialstatistics.enumeration.DistanceMethod;
import org.geotools.process.spatialstatistics.enumeration.SpatialConcept;
import org.geotools.process.spatialstatistics.enumeration.StandardizationMethod;
import org.geotools.util.KVP;
import org.geotools.util.logging.Logging;
import org.opengis.util.InternationalString;

/**
 * LocalLeesSProcessFactory
 * 
 * @author Minpa Lee, MangoSystem
 * 
 * @source $URL$
 */
public class LocalLeesSProcessFactory extends SpatialStatisticsProcessFactory {
    protected static final Logger LOGGER = Logging.getLogger(LocalLeesSProcessFactory.class);

    private static final String PROCESS_NAME = "LocalLeesS";

    /*
     * LocalLeesS(SimpleFeatureCollection inputFeatures, String inputField, SpatialConcept spatialConcept, DistanceMethod distanceMethod,
     * StandardizationMethod standardization, Double searchDistance) : SimpleFeatureCollection
     */

    public LocalLeesSProcessFactory() {
        super(new NameImpl(NAMESPACE, PROCESS_NAME));
    }

    @Override
    public Process create() {
        return new LocalLeesSProcess(this);
    }

    @Override
    public InternationalString getTitle() {
        return getResource("LocalLeesS.title");
    }

    @Override
    public InternationalString getDescription() {
        return getResource("LocalLeesS.description");
    }

    /** inputFeatures */
    public static final Parameter<SimpleFeatureCollection> inputFeatures = new Parameter<SimpleFeatureCollection>(
            "inputFeatures", SimpleFeatureCollection.class,
            getResource("LocalLeesS.inputFeatures.title"),
            getResource("LocalLeesS.inputFeatures.description"), true, 1, 1, null, null);

    /** inputField */
    public static final Parameter<String> inputField = new Parameter<String>("inputField",
            String.class, getResource("LocalLeesS.inputField.title"),
            getResource("LocalLeesS.inputField.description"), true, 1, 1, null, new KVP(
                    Parameter.OPTIONS, "inputFeatures.Number"));

    /** spatialConcept */
    public static final Parameter<SpatialConcept> spatialConcept = new Parameter<SpatialConcept>(
            "spatialConcept", SpatialConcept.class, getResource("LocalLeesS.spatialConcept.title"),
            getResource("LocalLeesS.spatialConcept.description"), false, 0, 1,
            SpatialConcept.INVERSEDISTANCE, null);

    /** distanceMethod */
    public static final Parameter<DistanceMethod> distanceMethod = new Parameter<DistanceMethod>(
            "distanceMethod", DistanceMethod.class, getResource("LocalLeesS.distanceMethod.title"),
            getResource("LocalLeesS.distanceMethod.description"), false, 0, 1,
            DistanceMethod.Euclidean, null);

    /** standardization */
    public static final Parameter<StandardizationMethod> standardization = new Parameter<StandardizationMethod>(
            "standardization", StandardizationMethod.class,
            getResource("LocalLeesS.standardization.title"),
            getResource("LocalLeesS.standardization.description"), false, 0, 1,
            StandardizationMethod.NONE, null);

    /** searchDistance */
    public static final Parameter<Double> searchDistance = new Parameter<Double>("searchDistance",
            Double.class, getResource("LocalLeesS.searchDistance.title"),
            getResource("LocalLeesS.searchDistance.description"), false, 0, 1, Double.valueOf(0.0),
            null);

    @Override
    protected Map<String, Parameter<?>> getParameterInfo() {
        HashMap<String, Parameter<?>> parameterInfo = new LinkedHashMap<String, Parameter<?>>();
        parameterInfo.put(inputFeatures.key, inputFeatures);
        parameterInfo.put(inputField.key, inputField);
        parameterInfo.put(spatialConcept.key, spatialConcept);
        parameterInfo.put(distanceMethod.key, distanceMethod);
        parameterInfo.put(standardization.key, standardization);
        parameterInfo.put(searchDistance.key, searchDistance);
        return parameterInfo;
    }

    /** result */
    public static final Parameter<SimpleFeatureCollection> RESULT = new Parameter<SimpleFeatureCollection>(
            "result", SimpleFeatureCollection.class, getResource("LocalLeesS.result.title"),
            getResource("LocalLeesS.result.description"));

    static final Map<String, Parameter<?>> resultInfo = new TreeMap<String, Parameter<?>>();
    static {
        resultInfo.put(RESULT.key, RESULT);
    }

    @Override
    protected Map<String, Parameter<?>> getResultInfo(Map<String, Object> parameters)
            throws IllegalArgumentException {
        return Collections.unmodifiableMap(resultInfo);
    }

}
