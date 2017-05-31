package com.edu.hrbeu.googlemap.pojo;


import java.util.ArrayList;

public class RoutesPOJO {

    private ArrayList<WayPoint> geocoded_waypoints;
    private ArrayList<Route> routes;
    private String status;

    public ArrayList<WayPoint> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public void setGeocoded_waypoints(ArrayList<WayPoint> geocoded_waypoints) {
        this.geocoded_waypoints = geocoded_waypoints;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Route{

        private Bound bounds;
        private String copyrights;
        private ArrayList<Leg> legs;
        private PolyLine overview_polyline;
        private String summary;

        public Bound getBounds() {
            return bounds;
        }

        public void setBounds(Bound bounds) {
            this.bounds = bounds;
        }

        public String getCopyrights() {
            return copyrights;
        }

        public void setCopyrights(String copyrights) {
            this.copyrights = copyrights;
        }

        public ArrayList<Leg> getLegs() {
            return legs;
        }

        public void setLegs(ArrayList<Leg> legs) {
            this.legs = legs;
        }

        public PolyLine getOverview_polyline() {
            return overview_polyline;
        }

        public void setOverview_polyline(PolyLine overview_polyline) {
            this.overview_polyline = overview_polyline;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }


        public class Leg{

            private Description distance;
            private Description duration;
            private String end_address;
            private Bound.Point end_location;
            private String start_address;
            private Bound.Point start_location;
            private ArrayList<Step>steps;

            public Description getDistance() {
                return distance;
            }

            public void setDistance(Description distance) {
                this.distance = distance;
            }

            public Description getDuration() {
                return duration;
            }

            public void setDuration(Description duration) {
                this.duration = duration;
            }

            public String getEnd_address() {
                return end_address;
            }

            public void setEnd_address(String end_address) {
                this.end_address = end_address;
            }

            public Bound.Point getEnd_location() {
                return end_location;
            }

            public void setEnd_location(Bound.Point end_location) {
                this.end_location = end_location;
            }

            public String getStart_address() {
                return start_address;
            }

            public void setStart_address(String start_address) {
                this.start_address = start_address;
            }

            public Bound.Point getStart_location() {
                return start_location;
            }

            public void setStart_location(Bound.Point start_location) {
                this.start_location = start_location;
            }

            public ArrayList<Step> getSteps() {
                return steps;
            }

            public void setSteps(ArrayList<Step> steps) {
                this.steps = steps;
            }


            public class Step{
                private Description distance;
                private Description duration;
                private Bound.Point end_location;
                private String html_instructions;
                private PolyLine polyline;
                private Bound.Point start_location;
                private String travel_mode;

                public Description getDistance() {
                    return distance;
                }

                public void setDistance(Description distance) {
                    this.distance = distance;
                }

                public Description getDuration() {
                    return duration;
                }

                public void setDuration(Description duration) {
                    this.duration = duration;
                }

                public Bound.Point getEnd_location() {
                    return end_location;
                }

                public void setEnd_location(Bound.Point end_location) {
                    this.end_location = end_location;
                }

                public String getHtml_instructions() {
                    return html_instructions;
                }

                public void setHtml_instructions(String html_instructions) {
                    this.html_instructions = html_instructions;
                }

                public PolyLine getPolyline() {
                    return polyline;
                }

                public void setPolyline(PolyLine polyline) {
                    this.polyline = polyline;
                }

                public Bound.Point getStart_location() {
                    return start_location;
                }

                public void setStart_location(Bound.Point start_location) {
                    this.start_location = start_location;
                }

                public String getTravel_mode() {
                    return travel_mode;
                }

                public void setTravel_mode(String travel_mode) {
                    this.travel_mode = travel_mode;
                }
            }

            public class Description{
                private String text;
                private String value;

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }

        public class Bound{

            private Point northeast;
            private Point southwest;

            public Point getNortheast() {
                return northeast;
            }

            public void setNortheast(Point northeast) {
                this.northeast = northeast;
            }

            public Point getSouthwest() {
                return southwest;
            }

            public void setSouthwest(Point southwest) {
                this.southwest = southwest;
            }

            public class Point{
                private double lat;
                private double lng;

                public double getLat() {
                    return lat;
                }

                public void setLat(double lat) {
                    this.lat = lat;
                }

                public double getLng() {
                    return lng;
                }

                public void setLng(double lng) {
                    this.lng = lng;
                }
            }
        }
    }

    public class WayPoint{
        private String geocoder_status;
        private String place_id;
        private ArrayList<String> types;

        public String getGeocoder_status() {
            return geocoder_status;
        }

        public void setGeocoder_status(String geocoder_status) {
            this.geocoder_status = geocoder_status;
        }

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public ArrayList<String> getTypes() {
            return types;
        }

        public void setTypes(ArrayList<String> types) {
            this.types = types;
        }
    }

    public class PolyLine{
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }
    }
}
