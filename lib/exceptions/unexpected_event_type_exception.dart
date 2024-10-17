/// {@template unexpected_event_type_exception}
/// Exception thrown when an unexpected event type is encountered.
/// {@endtemplate}
class UnexpectedEventTypeException implements Exception {
  /// {@macro unexpected_event_type_exception}
  UnexpectedEventTypeException(this.message);

  /// The exception message.
  final String message;

  @override
  String toString() {
    return 'UnexpectedEventTypeException: $message';
  }
}
