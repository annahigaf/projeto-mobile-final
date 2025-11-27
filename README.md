**🚸 Escola Conecta – Aplicativo de Transporte Escolar**

O **Escola Conecta** é um aplicativo Android desenvolvido para auxiliar a gestão do transporte escolar.  
O app permite organizar informações importantes como alunos, responsáveis, escolas, rotas, condutores e muito mais, além de possibilitar a publicação de avisos para os usuários.  

O sistema diferencia **alunos** e **administradores**, garantindo controle adequado de permissões.

---

**🖼️ Prints das Telas Principais**

Exemplo:

tela login:

<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/17af85cd-975d-4555-9a98-f8f6b0748982" />

pagina home com mural:

<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/8cf225a8-1a22-4ba2-86c4-3aab355d1dbd" />

menu:

<img width="738" height="1600" alt="image" src="https://github.com/user-attachments/assets/3142509c-0883-4490-896c-aa307c93d96f" />

**# 🛠 Tecnologias Utilizadas**

- **Kotlin**
- **Android Studio**
- **SQLite (banco de dados local)**
- **ViewBinding**
- **RecyclerView + Adapter**
- **Fragments**
- **Navigation Drawer**
- **Material Design**
- **Activities + Intents**

**Passo a passo para instalar e rodar**

1. git clone (https://github.com/annahigaf/projeto-mobile-final.git)

2️. Abrir no Android Studio

3. File → Open → selecione a pasta do projeto

Aguarde sincronizar o Gradle

3️⃣ Executar

Conecte um dispositivo físico ou configure um emulador

Clique no botão ▶️ Run
**🌐 Endpoints da API**

O projeto não utiliza API externa, pois funciona 100% offline usando SQLite local.
Todos os dados são armazenados e manipulados dentro do dispositivo.

Se futuramente houver API, essa seção pode ser atualizada.

**🧩 Como o CRUD Funciona**

O aplicativo utiliza um banco local SQLite gerenciado por uma classe personalizada:

Database.kt

**📌 Estrutura de tabelas:**
Usuários
Alunos
Responsáveis
Condutores
Turmas
Escolas
Rotas
Embarque/Desembarque
Mural de avisos
Relação aluno/rota

📌 As 4 operações estão implementadas:

✔ CREATE
fun insert(table: String, values: ContentValues): Long


Insere dados em qualquer tabela.

✔ READ
fun getAll(table: String): List<Map<String, String>>
fun getById(table: String, id: Int)

Busca dados completos ou individuais.

✔ UPDATE
fun update(table, values, "id=?", arrayOf(id))

Atualiza registros específicos.

✔ DELETE
fun delete(table, id)

Remove o registro.

**🔐 Controle de Acesso**

O login identifica automaticamente o tipo de usuário pelo e-mail:

Tipo	Exemplo	Permissões
Administrador	usuario@adm.com	CRUD completo + publicar avisos
Aluno/Responsável	usuario@aluno.com	Apenas visualizar

O tipo de usuário é enviado para a MainActivity:
intent.putExtra("tipoUsuario", tipoUsuario)

E lá o menu é configurado:
if (tipoUsuario == "ALUNO") {
    // apenas oculta acesso ao botão de criar aviso
}
O mural só exibe o botão “+” para quem for administrador.

**🎯 Funcionalidades Implementadas**

✔ Gerais
-Login
-Cadastro
-Splash screen
-Menu lateral com Navigation Drawer
-Modo admin vs aluno
-Salvar sessão via parâmetros

✔ Cadastros (CRUD completo)
-Alunos
-Responsáveis
-Escolas
-Turmas
-Condutores
-Rotas
-Relação aluno/rota

✔ Embarque/Desembarque
-Marcar status do aluno
-Lista filtrada por rota

✔ Mural de Avisos
-Criar aviso (admin)
-Listar avisos (todos)

**🧱 Funcionalidades Futuras (Backlog)**
-Notificações push para novos avisos
-Sincronização com banco online
-Dashboard administrativo
-Relatórios PDF
-Controle de presença diário

**👩‍💻 Autores e Contato**

Desenvolvido por:
- Anna Julia Higa
  
- Evelyn Mercês
   
- Leticia Macedo 

Contato: 
 -anna.farincho@aluno.faculdadeimpacta.com.br

-evelyn.merces@aluno.faculdadeimpacta.com.br

-leticia.macedo@aluno.faculdadeimpacta.com.br
