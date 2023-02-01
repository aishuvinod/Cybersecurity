import datetime

'''
    Represents a connection to a mock database from which you can read (query) and write to. 
    DAO = Data Access Object, a technique to abstract a database into an object with defined functions.
'''


class MockDatabaseDAO:

    '''
        Initalizes the mock database to empty.
    '''

    def __init__(self) -> None:
        self.keystrokes = []

    '''
        Writes to the database, storing information.
    '''
    # to add keystrokes (at the end)
    def write(self, key_pressed):
        self.keystrokes.append(key_pressed)

    '''
        Read every entry from the database with no formatting.
    '''

    def query(self):
        print(self.keystrokes)

    '''
        Read entries from the database from a certain date.
    '''

    def query_by_date(self, date_requested):
        for x in self.keystrokes :
            if x == date_requested:
                print(x)
