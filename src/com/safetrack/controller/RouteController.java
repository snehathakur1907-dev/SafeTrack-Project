package com.safetrack.controller;

import com.safetrack.dao.RouteDAO;
import com.safetrack.model.Route;

import java.util.List;

/**
 * Controller for route operations.
 * Views must use this class — no direct DAO/SQL in views.
 */
public class RouteController {

    private final RouteDAO routeDAO = new RouteDAO();

    /**
     * Returns all routes as string arrays for table display.
     * @return list of [id, source, destination, fare]
     */
    public List<String[]> getRoutes() {
        return routeDAO.getAllRoutes();
    }

    /**
     * Returns all routes as typed Route objects.
     * @return list of Route model objects
     */
    public List<Route> getRouteList() {
        return routeDAO.getRouteList();
    }

    /**
     * Adds a new route.
     * @param source      origin city
     * @param destination destination city
     * @param fare        ticket price
     */
    public void addRoute(String source, String destination, double fare) {
        routeDAO.addRoute(source, destination, fare);
    }

    /**
     * Deletes a route by ID.
     * @param id route primary key
     */
    public void deleteRoute(int id) {
        routeDAO.deleteRoute(id);
    }
}
