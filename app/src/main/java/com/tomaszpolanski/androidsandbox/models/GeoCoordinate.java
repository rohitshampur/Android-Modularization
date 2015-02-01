package com.tomaszpolanski.androidsandbox.models;

import com.tomaszpolanski.androidsandbox.utils.MathUtils;
import com.tomaszpolanski.androidsandbox.utils.option.Option;
import com.tomaszpolanski.androidsandbox.utils.result.Result;
import com.tomaszpolanski.androidsandbox.utils.result.ResultTuple;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class GeoCoordinate {
    private final double mLatitude;
    private final double mLongitude;

    private GeoCoordinate(final double latitude, final double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public Option<GeoCoordinate> withLatitude(final double latitude) {
        return create(latitude, mLongitude);
    }

    public Option<GeoCoordinate> withLongitude(final double longitude) {
        return create(mLatitude, longitude);
    }


    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }


    public static Option<GeoCoordinate> create(final double latitude, final double longitude) {
        return Option.asOption(latitude)
                     .filter(lat -> Math.abs(lat) <= 90.0)
                     .flatMap(lat -> Option.asOption(longitude)
                                           .filter(lng -> Math.abs(lng) <= 180.0)
                                           .map(lng -> new GeoCoordinate(lat, lng)));
    }


    public static Result<GeoCoordinate> fromString(final String stringCoordinate) {
        return Result.asResult(stringCoordinate)
                     .map(cString -> cString.split(","))
                     .filter(coordinateStringList -> coordinateStringList.length == 2, list -> "Invalid number of items: " + list.length)
                     .flatMap(coordinateList -> ResultTuple.create(
                             Result.tryAsResult(() -> Double.parseDouble(coordinateList[0])),
                             Result.tryAsResult(() -> Double.parseDouble(coordinateList[1]))))
                     .flatMap(tuple -> tuple.map((latitude, longitude) -> GeoCoordinate.create(latitude, longitude))
                                            .asResult("Coordinates out of bounds"));
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object o) {
        final double difference = 0.0001;
        return Option.asOption(o)
                     .filter(obj -> obj instanceof GeoCoordinate)
                     .map(obj -> (GeoCoordinate) obj)
                     .filter(other -> MathUtils.areEqual(other.mLongitude, mLongitude, difference))
                     .filter(other -> MathUtils.areEqual(other.mLatitude, mLatitude, difference)) != Option.NONE;
    }

    @Override
    public String toString() {
        final NumberFormat formatter = new DecimalFormat("#0.0000");
        return String.format("Latitude: %S, Longitude %S",
                formatter.format(mLatitude),
                formatter.format(mLongitude));
    }
}
