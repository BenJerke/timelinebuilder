-- Insert initial test data as part of schema creation
INSERT INTO tl_event (title, description, start_date_time, end_date_time) VALUES
                                                                              ('World War II Begins', 'Germany invades Poland, marking the beginning of World War II', '1939-09-01T00:00:00', '1939-09-02T00:00:00'),
                                                                              ('Moon Landing', 'Apollo 11 successfully lands on the moon with Neil Armstrong and Buzz Aldrin', '1969-07-20T20:17:00', '1969-07-21T02:56:00'),
                                                                              ('Fall of Berlin Wall', 'The Berlin Wall falls, symbolizing the end of the Cold War', '1989-11-09T18:53:00', '1989-11-10T12:00:00'),
                                                                              ('Internet Launch', 'The World Wide Web becomes publicly available', '1991-01-01T00:00:00', '1991-01-02T00:00:00'),
                                                                              ('Renaissance Begins', 'The Italian Renaissance period begins in Florence', '1400-01-01T00:00:00', '1400-12-31T23:59:59');

INSERT INTO tl_entity (name, description) VALUES
                                              ('Franklin Delano Roosevelt', 'US President during most of World War II'),
                                              ('Neil Armstrong', 'American astronaut and first person to walk on the Moon'),
                                              ('Ronald Reagan', 'American president during the Cold War era'),
                                              ('Tim Berners-Lee', 'British scientist who invented the World Wide Web'),
                                              ('Leonardo da Vinci', 'Italian Renaissance polymath, artist, and inventor');

INSERT INTO tl_tag (name, description) VALUES
                                           ('War', 'Military conflicts and battles'),
                                           ('Technology', 'Scientific and technological advancements'),
                                           ('Politics', 'Political events and government changes'),
                                           ('Space', 'Space exploration and astronomy'),
                                           ('Art', 'Artistic and cultural developments');

INSERT INTO tl_event_entities (event_id, entity_id) VALUES
                                                        (1, 1), -- WWII - Roosevelt
                                                        (2, 2), -- Moon Landing - Armstrong
                                                        (3, 3), -- Berlin Wall - Reagan
                                                        (4, 4), -- Internet - Berners-Lee
                                                        (5, 5); -- Renaissance - da Vinci

INSERT INTO tl_event_tags (event_id, tag_id) VALUES
                                                 (1, 1), -- WWII - War
                                                 (1, 3), -- WWII - Politics
                                                 (2, 2), -- Moon Landing - Technology
                                                 (2, 4), -- Moon Landing - Space
                                                 (3, 3), -- Berlin Wall - Politics
                                                 (4, 2), -- Internet - Technology
                                                 (5, 5), -- Renaissance - Art
                                                 (5, 2); -- Renaissance - Technology

INSERT INTO tl_event_related_events (event_id, related_event_id) VALUES
                                                                     (1, 3), -- WWII related to Fall of Berlin Wall
                                                                     (3, 1), -- Fall of Berlin Wall related to WWII
                                                                     (2, 4), -- Moon Landing related to Internet (technology advancement)
                                                                     (4, 2); -- Internet related to Moon Landing