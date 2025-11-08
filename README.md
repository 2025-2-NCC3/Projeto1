# FECAP - Fundação de Comércio Álvares Penteado



<p align="center">
  <img src="https://drive.google.com/uc?id=1yB-8CKtsZeu8x2ej59s_lK0ZXqzW24wI" alt="Imagem 1" width="250">
  <img src="https://drive.google.com/uc?id=1Jtk2rzAAhXDI7NLn5DyR_GM4-eb5cRBX" alt="Imagem 2" width="250">
  <img src="https://drive.google.com/uc?id=1egKQ6s0o8CbcBOBau22hn4qHsPHT2vw4" alt="Imagem 3" width="250">
  
</p>



# COMEDORIA DA TIA

## 👥 Integrantes do Projeto

<table>
  <tr>
    <td align="center">
      <a href="https://www.linkedin.com/in/estherolvr/">
        <img src="https://img.shields.io/badge/-Esther_Oliviera_Costa-0077B5?style=for-the-badge&logo=Linkedin&logoColor=white&labelColor=0A66C2"/>
      </a>
    </td>
    <td align="center">
      <a href="https://www.linkedin.com/in/fernandaloura/">
        <img src="https://img.shields.io/badge/-Fernanda_Loura_Da_Silva-0077B5?style=for-the-badge&logo=Linkedin&logoColor=white&labelColor=0A66C2"/>
      </a>
    </td>
    <td align="center">
      <a href="https://www.linkedin.com/in/higor-fonseca-santos/">
        <img src="https://img.shields.io/badge/-Higor_Fonseca-0077B5?style=for-the-badge&logo=Linkedin&logoColor=white&labelColor=0A66C2"/>
      </a>
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://www.linkedin.com/in/joaovictordefaria/">
        <img src="https://img.shields.io/badge/-João_Victor_de_Faria-0077B5?style=for-the-badge&logo=Linkedin&logoColor=white&labelColor=0A66C2"/>
      </a>
    </td>
    <td align="center">
      <a href="https://www.linkedin.com/in/thiffany-morais/">
        <img src="https://img.shields.io/badge/-Thiffany_Morais-0077B5?style=for-the-badge&logo=Linkedin&logoColor=white&labelColor=0A66C2"/>
      </a>
    </td>
    <td></td>
  </tr>
</table>

## Professores Orientadores: 
- [Katia Milani Lara Bossi](https://www.linkedin.com/in/katia-bossi/)  
- [Marco Aurelio Lima Barbosa](https://www.linkedin.com/in/marco-aurelio-lima-barbosa/)  
- [Rodrigo da Rosa](https://www.linkedin.com/in/rodrigo-da-rosa-phd/)  
- [Victor Bruno Alexander Rosetti de Quiroz](https://www.linkedin.com/in/victorbarq/) 
 </a>

## 1. Apresentação do Projeto

[Figma- Prototipo](https://www.figma.com/design/CelPnsa0C4rpSmoQcvDNiM/Comedoria-da-Tia-prototipe?node-id=0-1&p=)

A **Comedoria da Tia** é a cantina da FECAP responsável por atender os alunos nos intervalos da manhã e no horário de almoço. Atualmente, os processos de atendimento enfrentam desafios relacionados ao tempo limitado para a realização de pedidos, sobretudo devido às filas no caixa, que comprometem o tempo dos estudantes para suas refeições.

Diante desse cenário, propõe-se o desenvolvimento de um aplicativo mobile com o objetivo de otimizar o processo de compra de produtos da cantina. O aplicativo permitirá aos alunos realizarem seus pedidos e pagamentos antecipadamente, restando apenas a retirada dos produtos no balcão. O sistema também contará com uma interface administrativa para a cantina gerenciar o cardápio, os pedidos e relatórios operacionais.

---

## 🛠 Estrutura de pastas
```sh
📁 Projeto Interdisciplinar  
│  
├── 📁 documentos/  
│   ├── 📁 entrega-1/  
│   │   ├── 📄 Projeto Interdisciplinar - Entrega 1.pdf  
│   │   ├── 📄 Programação Orientada a Objetos - Entrega 1.pdf  
│   │   ├── 📄 Programação para Dispositivos Móveis - Entrega 1.pdf  
│   │   └── 📄 Análise Descritiva de Dados - Entrega 1.pdf  
│   │  
│   └── 📁 entrega-2/  
│       ├── 📄 Projeto Interdisciplinar - Entrega 2.pdf  
│       ├── 📄 Programação Orientada a Objetos - Entrega 2.pdf  
│       ├── 📄 Programação para Dispositivos Móveis - Entrega 2.pdf  
│       └── 📄 Análise Descritiva de Dados - Entrega 2.pdf  
│  
├── 📁 executaveis/  
│   └── 📁 apk/  
│       └── 📱 app-release.apk  
│  
├── 📁 imagens/  
│   └── 🖼️ logo.png  
│  
├── 📁 src/  
│   ├── 📁 entrega-1/  
│   │   ├── 📁 backend/  
│   │   └── 📁 frontend/  
│   │  
│   └── 📁 entrega-2/  
│       ├── 📁 frontend/  
│       └── 📁 backend/  
│  
└── 📄 README.md
```
## 🎯 2. Objetivos

### Objetivo Geral  
Desenvolver um aplicativo mobile (pelo menos para Android ou multiplataforma) que permita aos alunos da FECAP realizar pedidos e pagamentos de forma prática e antecipada na cantina "Comendaria da Tia", contribuindo para a melhoria da experiência de consumo e da gestão operacional da cantina.

### Objetivos Específicos  
- Eliminar a necessidade de enfrentar filas para pagamento na cantina.  
- Permitir à cantina gerenciar dinamicamente seu cardápio e pedidos.  
- Oferecer histórico de pedidos aos usuários.  
- Realizar simulação ou integração real com APIs de pagamento.  
- Desenvolver interfaces intuitivas e responsivas voltadas à experiência do usuário.  
- Análise de Dados.

---

## 🛠️ 3. Requisitos Funcionais

### Acesso Aluno (Cliente)  
- Auto cadastro e login de aluno.  
- Visualização do cardápio atualizado.  
- Realização de pedidos e escolha de itens.  
- Pagamento via API (Stripe, Mercado Pago, PagSeguro) ou simulado.  
- Visualização do histórico de pedidos realizados.

### Acesso Cantina (Empresa)  
- Login administrativo.  
- Cadastro e atualização do cardápio.  
- Visualização de pedidos pendentes e confirmação de retirada.  
- Baixa de pedidos (pedido entregue).  
- Relatórios gerenciais.

---

## 📚 4. Requisitos Não Funcionais  
- Interface intuitiva e responsiva (UX/UI).  
- Aplicação mobile compatível com Android (e preferencialmente iOS).  
- Armazenamento em nuvem.  
- Arquitetura orientada a objetos e/ou baseada em componentes reutilizáveis.  
- Disponibilidade mínima offline para visualização do cardápio.  
- Segurança no armazenamento de dados sensíveis.  
- Código modular e testável.

---

## 5. Tecnologias Sugeridas  
- Front-End: HTML5, CSS3, JavaScript, Bootstrap  
- Back-End: C# ou Node.js (Express)  
- Banco de Dados: MySQL ou SQLite  
- Ferramentas/Serviços Externos: API de pagamento (PagSeguro, Mercado Pago ou Stripe), API do Instagram/Facebook, Firebase ou Nodemailer

---

## 6. Etapas do Projeto  
1. Levantamento de Requisitos e Modelagem do Sistema.  
2. Design da Experiência e Protótipos.  
3. Desenvolvimento Backend e Banco de Dados.  
4. Desenvolvimento Mobile.  
5. Testes de Qualidade e Usabilidade.  
6. Documentação e Relatórios.  
7. Apresentação Final e Publicação.

---

## 7. Possíveis Extensões da Solução  
- Implementação de notificações push.  
- Sistema de pontuação ou fidelidade.  
- Feedback de produtos e atendimento.  
- Controle de estoque para a cantina.  
- Agendamento de pedidos para retirada futura.  
- Dashboard web para administração.

---

## 🛠 Instalação

<b>Android:</b>

Faça o download do arquivo .apk no seu celular.
Execute o APK e siga as instruções do seu dispositivo para instalar o aplicativo.

```sh
Coloque código do prompt de comnando se for necessário
```

## 💻 Configuração para Desenvolvimento
Passos para rodar o projeto

Clone o repositório:

-<a href="https://godotengine.org/download">GODOT</a>

```sh
make install
npm test
Coloque código do prompt de comnando se for necessário
```

## 📋 Licença/License
Comedoria da Tia © 2025 by Esther Oliveira Costa,Fernanda Loura da Silva, Higor Luiz Fonseca dos Santos, João Victor de Faria Santana, Thiffany Morais Vieira da Silva is licensed under CC BY 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0/

## 🎓 Referências

Aqui estão as referências usadas no projeto.

1. <https://github.com/iuricode/readme-template>
2. <https://github.com/gabrieldejesus/readme-model>
3. <https://chooser-beta.creativecommons.org/>
4. <https://freesound.org/>
5. <https://www.toptal.com/developers/gitignore>
6. Músicas por: <a href="https://freesound.org/people/DaveJf/sounds/616544/"> DaveJf </a> e <a href="https://freesound.org/people/DRFX/sounds/338986/"> DRFX </a> ambas com Licença CC 0.

