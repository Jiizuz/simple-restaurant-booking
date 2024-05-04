# Simple Restaurant Booking

HTTP server application that uses https://muserver.io/ to manage the bookings of a simple restaurant.

## Features

- **Create a Booking**: As a customer, you can request a booking at the restaurant specifying your desired date and time.
- **View Bookings**: As the restaurant owner, you can view all bookings for any given day to manage your restaurant's schedule effectively.

## Booking Details

Each booking includes the following information:
- **Customer Name**: The name of the person making the booking.
- **Table Size**: The number of people included in the booking (up to 10 people).
- **Date**: The date for which the booking is made.
- **Time**: Bookings are made for 2-hour time slots between 9 AM and 9 PM.

## Assumptions

- The restaurant has 10 tables.
- Only one booking can be made at a time per table.
- Bookings can be made any time before the reservation, as long as it's within operating hours.
- Past bookings can also be stored for record-keeping.
- All operations are conducted in the UTC timezone.
- Customers and the restaurant owner can view all bookings without restrictions.
- Customers can book multiple tables if needed.
