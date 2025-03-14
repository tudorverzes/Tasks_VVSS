Aplicația gestionează task-urile unei persoane active. Datele sunt salvate și preluate dintr-un fișier binar sau text, care este actualizat automat la închiderea aplicației. La inițializare, aplicația verifică existența fișierului de date și îl creează dacă nu există.

Funcționalități:
- F01. Adăugarea unui task nou cu detaliile: descrierea, data și ora de început, data și ora de sfârșit.
  - Dacă task-ul este repetitiv, atunci se indică intervalul de timp la care se va repeta, dat ca număr de ore și minute.
  - Task-ul poate fi activ sau nu.
  - Se validează ca data de început să fie ulterioară momentului curent și anterioară datei de sfârșit.
  - Se verifică suprapunerile cu alte task-uri deja existente.

- F02. Afișarea task-urilor planificate într-o anumită perioadă de timp, precizată ca dată și oră de început și dată și oră de sfârșit.
  - Posibilitatea de a sorta și filtra task-urile după criterii precum: dată, prioritate, stare (activ/completat).

- F03. Afișarea informațiilor referitoare la un anumit task.

- F04. Modificarea detaliilor unui task.
  - Se permite modificarea oricărui atribut al unui task, inclusiv dacă acesta este repetitiv sau nu.
  - Se verifică suprapunerile cu alte task-uri existente după modificare.

- F05. Marcarea unui task ca finalizat.

- F06. Ștergerea unui task.

- F07. Persistența datelor:
  - La închiderea aplicației, fișierul original este suprascris pentru a salva modificările.
  - La pornirea aplicației, datele sunt reîncărcate din fișier.