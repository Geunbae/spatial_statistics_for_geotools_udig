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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.Process;
import org.geotools.process.ProcessException;
import org.geotools.process.ProcessFactory;
import org.geotools.process.spatialstatistics.core.Params;
import org.geotools.process.spatialstatistics.enumeration.RadialType;
import org.geotools.process.spatialstatistics.operations.PolarGridsOperation;
import org.geotools.text.Text;
import org.geotools.util.NullProgressListener;
import org.geotools.util.logging.Logging;
import org.opengis.util.ProgressListener;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Creates a radial polar grids from geometry(centroid).
 * 
 * @author Minpa Lee, MangoSystem
 * 
 * @source $URL$
 */
public class PolarGridsFromGeometryProcess extends AbstractStatisticsProcess {
    protected static final Logger LOGGER = Logging.getLogger(PolarGridsFromGeometryProcess.class);

    private boolean started = false;

    public PolarGridsFromGeometryProcess(ProcessFactory factory) {
        super(factory);
    }

    public ProcessFactory getFactory() {
        return factory;
    }

    public static SimpleFeatureCollection process(Geometry origin, String radius,
            RadialType radialType, Integer sides, ProgressListener monitor) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(PolarGridsFromGeometryProcessFactory.origin.key, origin);
        map.put(PolarGridsFromGeometryProcessFactory.radius.key, radius);
        map.put(PolarGridsFromGeometryProcessFactory.radialType.key, radialType);
        map.put(PolarGridsFromGeometryProcessFactory.sides.key, sides);

        Process process = new PolarGridsFromGeometryProcess(null);
        Map<String, Object> resultMap;
        try {
            resultMap = process.execute(map, monitor);
            return (SimpleFeatureCollection) resultMap
                    .get(PolarGridsFromGeometryProcessFactory.RESULT.key);
        } catch (ProcessException e) {
            LOGGER.log(Level.FINER, e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> input, ProgressListener monitor)
            throws ProcessException {
        if (started)
            throw new IllegalStateException("Process can only be run once");
        started = true;

        if (monitor == null)
            monitor = new NullProgressListener();
        try {
            monitor.started();
            monitor.setTask(Text.text("Grabbing arguments"));
            monitor.progress(10.0f);

            Geometry origin = (Geometry) Params.getValue(input,
                    PolarGridsFromGeometryProcessFactory.origin, null);
            String radius = (String) Params.getValue(input,
                    PolarGridsFromGeometryProcessFactory.radius, null);
            if (origin == null || radius == null || radius.length() == 0) {
                throw new NullPointerException("origin, radius parameters required");
            }
            RadialType radialType = (RadialType) Params.getValue(input,
                    PolarGridsFromGeometryProcessFactory.radialType,
                    PolarGridsFromGeometryProcessFactory.radialType.sample);
            Integer sides = (Integer) Params.getValue(input,
                    PolarGridsFromGeometryProcessFactory.sides,
                    PolarGridsFromGeometryProcessFactory.sides.sample);

            monitor.setTask(Text.text("Processing ..."));
            monitor.progress(25.0f);

            if (monitor.isCanceled()) {
                return null; // user has canceled this operation
            }

            // start process
            String[] arrDistance = radius.split(",");
            double[] bufferRadius = new double[arrDistance.length];
            for (int k = 0; k < arrDistance.length; k++) {
                try {
                    bufferRadius[k] = Double.parseDouble(arrDistance[k].trim());
                } catch (NumberFormatException nfe) {
                    throw new NumberFormatException(nfe.getMessage());
                }
            }
            
            PolarGridsOperation operation = new PolarGridsOperation();
            SimpleFeatureCollection resultFc = operation.execute(origin, bufferRadius, sides, radialType);
            // end process

            monitor.setTask(Text.text("Encoding result"));
            monitor.progress(90.0f);

            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put(PolarGridsFromGeometryProcessFactory.RESULT.key, resultFc);
            monitor.complete(); // same as 100.0f

            return resultMap;
        } catch (Exception eek) {
            monitor.exceptionOccurred(eek);
            return null;
        } finally {
            monitor.dispose();
        }
    }
}
