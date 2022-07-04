package com.boomaa.opends.usb;

public interface IOKitFlags {
    int AXIS_DEFAULT_MIN_VALUE = 0;
    int AXIS_DEFAULT_MAX_VALUE = 65536;

    String kIOHIDTransportKey                  = "Transport";
    String kIOHIDVendorIDKey                   = "VendorID";
    String kIOHIDVendorIDSourceKey             = "VendorIDSource";
    String kIOHIDProductIDKey                  = "ProductID";
    String kIOHIDVersionNumberKey              = "VersionNumber";
    String kIOHIDManufacturerKey               = "Manufacturer";
    String kIOHIDProductKey                    = "Product";
    String kIOHIDSerialNumberKey               = "SerialNumber";
    String kIOHIDCountryCodeKey                = "CountryCode";
    String kIOHIDLocationIDKey                 = "LocationID";
    String kIOHIDDeviceUsageKey                = "DeviceUsage";
    String kIOHIDDeviceUsagePageKey            = "DeviceUsagePage";
    String kIOHIDDeviceUsagePairsKey           = "DeviceUsagePairs";
    String kIOHIDPrimaryUsageKey               = "PrimaryUsage";
    String kIOHIDPrimaryUsagePageKey           = "PrimaryUsagePage";
    String kIOHIDMaxInputReportSizeKey         = "MaxInputReportSize";
    String kIOHIDMaxOutputReportSizeKey        = "MaxOutputReportSize";
    String kIOHIDMaxFeatureReportSizeKey       = "MaxFeatureReportSize";

    String kIOHIDElementKey                    = "Elements";

    String kIOHIDElementCookieKey              = "ElementCookie";
    String kIOHIDElementTypeKey                = "Type";
    String kIOHIDElementCollectionTypeKey      = "CollectionType";
    String kIOHIDElementUsageKey               = "Usage";
    String kIOHIDElementUsagePageKey           = "UsagePage";
    String kIOHIDElementMinKey                 = "Min";
    String kIOHIDElementMaxKey                 = "Max";
    String kIOHIDElementScaledMinKey           = "ScaledMin";
    String kIOHIDElementScaledMaxKey           = "ScaledMax";
    String kIOHIDElementSizeKey                = "Size";
    String kIOHIDElementReportSizeKey          = "ReportSize";
    String kIOHIDElementReportCountKey         = "ReportCount";
    String kIOHIDElementReportIDKey            = "ReportID";
    String kIOHIDElementIsArrayKey             = "IsArray";
    String kIOHIDElementIsRelativeKey          = "IsRelative";
    String kIOHIDElementIsWrappingKey          = "IsWrapping";
    String kIOHIDElementIsNonLinearKey         = "IsNonLinear";
    String kIOHIDElementHasPreferredStateKey   = "HasPreferredState";
    String kIOHIDElementHasNullStateKey        = "HasNullState";
    String kIOHIDElementUnitKey                = "Unit";
    String kIOHIDElementUnitExponentKey        = "UnitExponent";
    String kIOHIDElementNameKey                = "Name";
    String kIOHIDElementValueLocationKey       = "ValueLocation";
    String kIOHIDElementDuplicateIndexKey      = "DuplicateIndex";
    String kIOHIDElementParentCollectionKey    = "ParentCollection";

    int UP_GENERIC_DESKTOP = 0x01;
    int UP_KEYBOARD = 0x07;
    int UP_BUTTON = 0x09;

    int USAGE_GAMEPAD = 0x05;
    int USAGE_JOYSTICK = 0x04;

    int ET_MISC = 1;
    int ET_BUTTON = 2;
    int ET_AXIS = 3;

    int GD_USAGE_AXISMIN = 0x30;
}
