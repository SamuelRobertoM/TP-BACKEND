# Google Maps Distance Matrix API Integration

## Overview
This document describes the integration of Google Maps Distance Matrix API to replace the Haversine formula with real-world distance and time calculations.

## Configuration

### Application Properties
Located in: `src/main/resources/application.properties`

```properties
google.maps.api-key=YOUR_API_KEY_HERE
google.maps.base-url=https://maps.googleapis.com/maps/api
```

**Important**: Replace `YOUR_API_KEY_HERE` with your actual Google Maps API key from Google Cloud Console.

### RestClient Configuration
Located in: `src/main/java/utn/frc/isi/backend/tpi_Integrador/config/RestClientConfig.java`

- Bean name: `googleMapsRestClient`
- Base URL: Configured from `google.maps.base-url` property
- Uses Spring's RestClient for HTTP communication

## DTOs Structure

All DTOs are located in: `src/main/java/utn/frc/isi/backend/tpi_Integrador/dtos/googlemaps/`

### GoogleDistanceMatrixResponse
Root response object from the API.

**Fields:**
- `status`: API response status ("OK", "INVALID_REQUEST", etc.)
- `originAddresses`: List of formatted origin addresses
- `destinationAddresses`: List of formatted destination addresses
- `rows`: List of Row objects containing distance/duration data

### Row
Represents one origin's distances to all destinations.

**Fields:**
- `elements`: List of Element objects (one per destination)

### Element
Contains distance and duration for one origin-destination pair.

**Fields:**
- `status`: Element status ("OK", "NOT_FOUND", "ZERO_RESULTS")
- `distance`: Distance object
- `duration`: Duration object

### Distance
Distance information in human-readable and numeric formats.

**Fields:**
- `text`: Human-readable distance (e.g., "647 km")
- `value`: Distance in meters (Long)

### Duration
Duration information in human-readable and numeric formats.

**Fields:**
- `text`: Human-readable duration (e.g., "7 hours 30 mins")
- `value`: Duration in seconds (Long)

## API Endpoint

**Distance Matrix API:**
```
GET https://maps.googleapis.com/maps/api/distancematrix/json
```

**Query Parameters:**
- `origins`: Lat/long coordinates (e.g., "-31.4201,-64.1888")
- `destinations`: Lat/long coordinates (e.g., "-34.6037,-58.3816")
- `key`: Your API key
- `units`: "metric" (optional, default)
- `language`: "es" (optional)

**Example Request:**
```
GET /distancematrix/json?origins=-31.4201,-64.1888&destinations=-34.6037,-58.3816&key=YOUR_API_KEY
```

**Example Response:**
```json
{
  "status": "OK",
  "origin_addresses": ["Córdoba, Argentina"],
  "destination_addresses": ["Buenos Aires, Argentina"],
  "rows": [
    {
      "elements": [
        {
          "status": "OK",
          "distance": {
            "text": "710 km",
            "value": 710000
          },
          "duration": {
            "text": "8 hours 30 mins",
            "value": 30600
          }
        }
      ]
    }
  ]
}
```

## Next Steps

1. **Create GoogleMapsService**
   - Inject `RestClient` bean
   - Implement method to call Distance Matrix API
   - Handle API responses and errors
   - Convert meters to km, seconds to hours

2. **Modify RutaService**
   - Inject `GoogleMapsService`
   - Update `calcularRutasTentativas()` to use Google Maps
   - Implement fallback to Haversine if API fails
   - Update cost calculations based on real distances

3. **Error Handling**
   - Handle API errors (ZERO_RESULTS, OVER_QUERY_LIMIT, etc.)
   - Log API failures
   - Graceful fallback to Haversine formula

4. **Testing**
   - Test with real API key
   - Test error scenarios
   - Compare results with Haversine calculations
   - Verify cost calculations

## Benefits

- **Real-world accuracy**: Considers actual road networks
- **Traffic awareness**: Can include traffic data if enabled
- **Better ETAs**: More accurate time estimates
- **Professional grade**: Production-ready distance calculations

## Current State

✅ Configuration added to application.properties  
✅ RestClientConfig created with Bean  
✅ All 5 DTOs created in googlemaps package  
✅ GoogleMapsClient implemented with proper error handling  
✅ GoogleMapsService created as service layer  
✅ RutaService updated to use Google Maps (removed Haversine)  
✅ SolicitudService cleaned (removed Haversine calculation)  
✅ Project compiles successfully (51 source files)  
✅ All tests passing (1/1)  
⏳ Testing with real API key pending  
⏳ Real-world testing pending

## Changes Summary

### Files Created
1. **RestClientConfig.java** - Spring configuration for RestClient bean
2. **Distance.java** - DTO for distance data (text, value in meters)
3. **Duration.java** - DTO for duration data (text, value in seconds)
4. **Element.java** - DTO for element data (status, distance, duration)
5. **Row.java** - DTO for row data (list of elements)
6. **GoogleDistanceMatrixResponse.java** - Root response DTO
7. **GoogleMapsClient.java** - Client layer for Google Maps API calls
8. **GoogleMapsService.java** - Service layer for business logic

### Files Modified
1. **application.properties** - Added Google Maps configuration
2. **RutaService.java** - Removed Haversine method, updated calcularRutasTentativas() and asignarRutaASolicitud() to use Google Maps
3. **SolicitudService.java** - Removed Haversine method, simplified crearNuevaSolicitud() to defer distance/time calculation

### Code Removed
- ❌ `calcularDistanciaHaversine()` from RutaService
- ❌ `calcularDistancia()` from SolicitudService
- ❌ All Haversine formula calculations
- ❌ Immediate distance/time calculation in solicitud creation

### New Behavior
- **Solicitud Creation (RF#1)**: No longer calculates distance/time immediately. Sets values to 0, defers to RF#3.
- **Tentative Routes (RF#3)**: Now uses Google Maps API for real-world distances and times.
- **Assign Route (RF#4)**: Now uses Google Maps API to calculate each tramo's distance and time.
- **Error Handling**: Throws RuntimeException if Google Maps API fails (no fallback).

## Important Notes

⚠️ **API Key Required**: Replace `YOUR_API_KEY_HERE` in application.properties with a valid Google Maps API key.

⚠️ **No Fallback**: The system now depends entirely on Google Maps API. If the API is unavailable, route calculations will fail.

⚠️ **Cost Implications**: Google Maps API has usage limits and costs. Monitor your API usage.

## Testing Checklist

Before deploying to production:

- [ ] Replace `YOUR_API_KEY_HERE` with real API key
- [ ] Test RF#3 (GET /api/solicitudes/{id}/rutas/tentativas)
- [ ] Test RF#4 (POST /api/solicitudes/{id}/asignar-ruta)
- [ ] Verify distances match expected values
- [ ] Verify times are realistic
- [ ] Test error handling (invalid coordinates, API failures)
- [ ] Monitor API usage and costs
- [ ] Consider implementing fallback to Haversine if needed
